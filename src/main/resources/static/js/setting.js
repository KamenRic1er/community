$(function(){
    $("#uploadForm").submit(upload);
});

function upload() {
    $.ajax({
        url: "http://up-cn-east-2.qiniup.com",
        method: "post",
        // 默认情况下浏览器会将表单内容转为字符串，而我们是上传文件，所以需要拒绝转换成字符串
        processData: false,
        // 不指定数据类型，让浏览器决定
        contentType: false,
        // [0]是将jQuery对象转为js对象
        data: new FormData($("#uploadForm")[0]),
        success: function(data) {
            if(data && data.code == 0) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function(data) {
                        // UserController中108行可知如果正确执行返回的就是json格式的数据，我们可以将其主动转换为对象
                        data = $.parseJSON(data);
                        if(data.code == 0) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败!");
            }
        }
    });
    return false;
}