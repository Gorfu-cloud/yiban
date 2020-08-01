layui.use('table', function(){
    var table = layui.table;
    $.ajaxSetup({
        xhrFields: {
           withCredentials: true
        },
    });

    var yhm = $("#yhm");
    var mm = $("#mm");
    var xn = $("#xn");
    var xq = $("#xq");
    // var isLogin =  [[]];

    var kcmc = null;
    var jxb_id = null;

    //第一个实例
    table.render({
        elem: '#demo'
        ,height: 360
        ,contentType: "application/x-www-form-urlencoded"
        ,url: 'getGrade' //数据接口
        ,where: {year:xn.val(), term:xq.val()} //如果无需传递额外参数，可不加该参数
        // ,page: true
        ,limits:[7,14]
        ,parseData: function(res) { //res 即为原始返回的数据
            var length = res.items.length;
            if(length > 8){
                layer.msg("共"+length+"条成绩信息，" + "可划动查看！");
            }else {
                layer.msg("已为您查到"+length+"条成绩信息");
            }

            return {
                "code": 0, //解析接口状态
                "data": res.items, //解析数据列表
                "count": res.items.length
            };
        }
        ,method: 'post' //如果无需自定义HTTP类型，可不加该参数
        ,id:'testReload'
        ,cols: [
            [ //表头
                {field: 'kcmc',title: '课程名', fixed: 'left'}
                ,{field: 'xf',width:50, title: '学分'}
                ,{field: 'cj',width:50, title: '成绩'
                ,templet: function (d) {
                    if (d.cj < 60) {
                        return '<b style="color:red;font-size: 0.9rem">' + d.cj + '</b>'
                    }else {
                        return d.cj;
                    }
                }}
                ,{field: 'jd',width:50, title: '绩点'}
                ,{fixed: 'right',width:35, align:'center', toolbar: '#barDemo'}
            ]
        ]
    });

    table.on('tool(test)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
        var data = obj.data //获得当前行数据
            ,layEvent = obj.event; //获得 lay-event 对应的值
        if(layEvent == 'edit'){
            layer.open({
                type: 1,
                title: "成绩详情",
                area: ['310px', '300px'],
                content: $("#popUpdateTest")//引用的弹出层的页面层的方式加载修改界面表单
            });

            //动态向表传递赋值可以参看文章进行修改界面的更新前数据的显示，当然也是异步请求的要数据的修改数据的获取
            setFormValue(obj,data);
        }
    });
    function setFormValue(obj,data) {
        kcmc = data.kcmc;
        jxb_id = data.jxb_id;
        table.reload('testReload1', {
            url: 'Detail' //数据接口
            ,where: {year:xn.val(), term:xq.val(),kcmc: kcmc,jxb_id: jxb_id} //如果无需传递额外参数，可不加该参数
        });
    }

    $('#btn_find').on('click', function(){
        console.log("查询");
        table.reload('testReload', {
            where: {year:xn.val(), term:xq.val()} //设定异步数据接口的额外参数
        });
    });



    table.render({
        elem: '#detail'
        , height: 180
        , parseData: function (res) { //res 即为原始返回的数据
            return {
                "code": 0, //解析接口状态
                "data": res.data //解析数据列表
            };
        }
        , method: 'post'
        ,id:'testReload1'
        , cols: [
            [ //表头
                {field: 'cjfx', width: 120, title: '成绩分项', fixed: 'left'}
                , {field: 'grade', width: 100, title: '成绩'}
                , {field: 'ratio', width: 100, title: '比例'}
            ]
        ]
    });

});