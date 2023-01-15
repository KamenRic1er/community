package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 少不入川
 * @Date: 2023/1/14 17:27
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    // 使用该注解意味着在将类放入服务之前必须调用此方法。
    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while((keyword = reader.readLine()) != null){
                this.addKeyword(keyword);
            }

        } catch (Exception e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树中
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if(i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }

    }

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;

        // 结果
        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);

            // 跳过符号
            if(isSymbol(c)){
                // 若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头还是中间，指针3都向下走一步
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                begin++;
                position = begin;
                // 指针1重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                // 发现敏感词
                sb.append(REPLACEMENT);
                // 进入下个节点
                position++;
                begin = position;
                // 指针1重新指向根节点
                tempNode = rootNode;
            }else{
                // 检查下一个字符
                position++;
            }

        }
        // 将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    private boolean isSymbol(Character c){
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    // 前缀树
    private class TrieNode{

        // 关键字结束标识
        private boolean isKeywordEnd = false;

        // 当前节点的子节点,Key是下级字符，Value是下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        // 取出子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
