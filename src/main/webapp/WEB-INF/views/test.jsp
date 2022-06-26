<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>위시캣 그리드 프로젝트 테스트</title>
    <meta charset="utf-8">
    <link href="/content/shared/styles/examples-offline.css" rel="stylesheet">
    <link href="/styles/kendo.common.min.css" rel="stylesheet">
    <link href="/styles/kendo.rtl.min.css" rel="stylesheet">
    <link href="/styles/kendo.default.min.css" rel="stylesheet">
    <!-- <link href="/styles/kendo.default.mobile.min.css" rel="stylesheet"> -->
    <script src="/kendo/js/jquery.min.js"></script>
    <script src="/kendo/js/jszip.min.js"></script>
    <script src="/kendo/js/kendo.all.min.js"></script>
    <script src="/content/shared/js/console.js"></script>

    <script src="/content/shared/js/orders.js"></script>
</head>
<body>

<br/>
<br/>
<br/>

* 위시캣 그리드 프로젝트 테스트
<br/>
1. 컬럼 순서 변경
<br/>
2. 컬럼 사이즈 변경
<br/>
3. 컬럼 감추기
<br/>
4. 멀티 정렬(sort)
<br/><br/>
<form name="frmGrid" action="/test" method="post" >
    <input type="text" name="config_name" id="config_name"  />
    <input type="button" value="설정저장" onClick="fn_save()"/>

    &nbsp;&nbsp;&nbsp;&nbsp;

    설정:<select name="sel_config" id="sel_config">
        <c:if test="${not empty names}">  
            <c:forEach items="${names}" var="name" varStatus="status">
                <option value="${name}" <c:if test="${configName eq name}"> selected </c:if> >${name}</option>
            </c:forEach>                
        </c:if>
        
    </select>
    <input type="button" value="불러오기" onclick="apply()" />
    <input type="button" value="삭제" onclick="deleteConfig()"/>
</form>
<div id="example">
    <div id="grid"></div>
    <script>
        $(document).ready(function () {
            if (kendo.support.browser.msie !== true) {
                $("#grid").kendoGrid({
                    dataSource: {
                        // type: "odata",
                        // transport: {
                        //     read: "https://demos.telerik.com/kendo-ui/service/Northwind.svc/Orders"
                        // },
                        data:orders,
                        schema: {
                            model: {
                                fields: {
                                    OrderID: { type: "number" },
                                    ShipCountry: { type: "string" },
                                    ShipName: { type: "string" },
                                    ShipCity: { type: "string" },
                                    ShipAddress: { type: "string" },
                                    RequiredDate: { type: "date" }
                                }
                            }
                        },
                        pageSize: 30
                    },
                    height: 540,
                    // sortable: true,
                    sortable: {
                            mode: "multiple",
                            allowUnsort: true
                        },
                    reorderable: true,
                    groupable: false,
                    resizable: true,
                    filterable: false,
                    columnMenu: true,
                    pageable: true,
                    columns: [
            <c:if test="${not empty cols}">      
                <c:forEach items="${cols}" var="col" varStatus="status">
                    {
                        field: "${col.colId}",
                        title: "${col.colName}",
                        stickable: true,
                        width: ${col.colWidth},
                        hidden: ${col.colHidden}
                    } <c:if test="${not status.last}">, </c:if>
                </c:forEach>
            </c:if>
            <c:if test="${empty cols}">   
                {
                        field: "OrderID",
                        title: "주문번호",
                        stickable: true,
                        width: 80
                    },
                    {
                        field: "CustomerID",
                        title: "아이디",
                        stickable: true,
                        width: 150
                    },
                    {
                        field: "ShipName",
                        title: "성명",
                        stickable: true,
                        width: 250
                    }, {
                        field: "ShipCountry",
                        title: "국가",
                        stickable: true,
                        width: 100
                    }, {
                        field: "ShipAddress",
                        title: "주소",
                        sticky: true,
                        width: 300
                    }, {
                        field: "ShipCity",
                        title: "도시",
                        stickable: true,
                        width: 120
                    }, {
                        field: "ShipPostalCode",
                        title: "우편번호",
                        stickable: true,
                        width: 80
                    },
                    {
                        field: "ShipVia",
                        title: "운송수단",
                        stickable: true,
                        width: 80
                    }
            </c:if>                  
                    

                    ]
                });
            } else {
                $("#grid").html("Sticky columns are not supported in IE!")
            }

            <c:if test="${not empty sorts}">  
                var grid = $("#grid").data("kendoGrid");
                grid.dataSource.sort(
                [
                <c:forEach items="${sorts}" var="item" varStatus="status">
                    {field: "${item.colId}", dir: "${item.sortDir}"} <c:if test="${not status.last}">, </c:if>
                </c:forEach>
                ]
                );
            </c:if>


        });

        

	function fn_save() {

        var name = $("#config_name").val()

	    console.log("name=" + name);

        var arr = new Array();
        

		var i = 1;
		$("*[data-field]").each(function(){
			if ($(this).attr("scope") === 'col')
			{
				
                var obj = new Object();
                obj.saveName = name;
                obj.colId =  $(this).attr("data-field");
                obj.colName = getTitle(obj.colId);
                obj.colWidth =  parseInt($(this).width() + 33.4);
                if ($(this).css('display') === 'none') {
                    obj.colHidden = "true";
                }
                else {
                    obj.colHidden = "false";
                }
                obj.colSeq = i++;
                obj.colSort = "N";
                obj.colAlign = "left";
                arr.push(obj);
			}
		});

        console.log(JSON.stringify(arr));

        $.ajax({

            url:"/test/saveGridConfig",
            type: "POST",
            contentType: "application/json; charset=UTF-8",
            data:JSON.stringify(arr),
            dataType:"json",
            success: function(data) {
                result = data.resultCode;
                alert("저장했습니다: " + name);
            },
            error:function(request, status, e) {
                alert("저장에 실패했습니다.");
                console.log(e);
            }
        });

        var dataSource = $("#grid").data("kendoGrid").dataSource;
        var sort = dataSource.sort();

        var sortArr = new Array();

        

        if(sort != undefined) {
            for (i = 0; i < sort.length; i++) {
                var obj = new Object();

                obj.saveName = name;
                obj.sortSeq = i;
                obj.colId = sort[i].field;
                obj.sortDir = sort[i].dir;
                sortArr.push(obj);
            }
            console.log(JSON.stringify(sortArr));

            $.ajax({

                url:"/test/saveGridSort",
                type: "POST",
                contentType: "application/json; charset=UTF-8",
                data:JSON.stringify(sortArr),
                dataType:"json",
                success: function(data) {
                    result = data.resultCode;
                },
                error:function(request, status, e) {
                    console.log(e);
                }
                });


        } else {
            console.log("sort 없음");
        }

        

        // $("#sel_config").empty();

        // $("#sel_config").append($('<option>', {
        //     value: '',
        //     text:  '---- 기본 -----'      
        // }));


        $("#sel_config").append($('<option>', {
            value: name,
            text:  name      
        }));

        $("#sel_config").val(name).attr("selected", "selected");

        $("#config_name").val('');
	}

    function deleteConfig() {
        var name = $("#sel_config option:selected").val();
        if (name === '0.default') {
            alert("기본설정은 삭제불가");
            return;
        }

        var obj = new Object();
        obj.saveName = name;

        console.log(obj);

        $.ajax({

            url:"/test/deleteGridConfig",
            type: "POST",
            // contentType: "charset=UTF-8",
            data:obj,
            // dataType:"json",
            success: function(data) {
                result = data.resultCode;
                alert("삭제했습니다");
                location.href="/test";
            },
            error:function(request, status, e) {
                alert("삭제에 실패했습니다.");
                console.log(e);
            }


        });

    }

    function apply() {
        var name = $("#sel_config option:selected").val();

        location.href="/test?configName=" + name;
    }


    function controll() {

        var grid = $('#grid').getKendoGrid();

        // var options = grid.options;
        // // options.columns[1].sortable = false;
        // // grid.setOptions(options);

        // grid.reorderColumn(1, grid.columns[0]);

        // for (i = 0; i < grid.columns.length; i++) {
        //     console.log(i + "." + grid.columns[i].field + ":" + grid.columns[i].title);
        // }
        console.log(grid.columns);
        for (i = 0; i < grid.columns.length; i++) {
            var tmp = '';
            tmp += grid.columns[i].field + ' - ';
            tmp += grid.columns[i].title + ' - ';
        }    


    }

    function getTitle(colId) {
        var grid = $('#grid').getKendoGrid();
        for (i = 0; i < grid.columns.length; i++) {
            if (grid.columns[i].field === colId) {
                return grid.columns[i].title;
            }
            
        }
    }


    function DisbaleSort(){ 	
        var columnsToModifySort = ["field1", "field2"]; 	
        var grid = $('#grid').getKendoGrid(); 	
        var options = grid.options; 	
        var columns = options.columns;  	
        
        _.each(columnsToModifySort, function (fieldName) {
             		var index = columns.findIndex(function (v, i) 
                        { return grid.columns[i].field === fieldName; }); 		
                        if (index >= 0) { 			
                            columns[index].sortable = false; 			
                            grid.setOptions(options); 		
                        } 	}); }

    
    
    </script>
</div>


<input type="button" value="클릭" onclick="controll()" />

</body>
</html>