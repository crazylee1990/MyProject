
var req;
function selectID(id) {
    var selectID = document.getElementById("selectID");
    var tmp=selectID.value;
    selectID.value = id;
    var selectItem = document.getElementById("selectID"+id);
    if( tmp!=-1){
        var lastItem = document.getElementById("selectID"+tmp);
        lastItem.setAttribute("bgcolor","#ECF7FF");
    }
    selectItem.setAttribute("bgcolor","#A0D4F8");
    
}

function selectByTag() {
    // window.alert("request sent test");
    var tag = document.getElementById("ooptions").value;

    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    } else {
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }
    // window.alert("before handle test");
    req.onreadystatechange = handleTagResponse;
    req.open("GET", "/TT/sellingItem/itemsByTag?tag=" + tag, true);
    req.send(); 
}

function handleTagResponse() {
    // window.alert("request handled test");

    if (req.readyState != 4 || req.status != 200) {
        return;
    }
    // Removes the old to-do list items
    var list = document.getElementById("sellingItems");
    while (list.hasChildNodes()) {
        list.removeChild(list.firstChild);
    }

    // Parses the XML response to get a list of DOM nodes representing items
    var xmlData = req.responseXML;
    var items = xmlData.getElementsByTagName("item");
    // Adds each new todo-list item to the list
    // window.alert("request handled test:"+items.length);
    for (var i = 0; i < items.length; ++i) {
        // Parses the item id and text from the DOM
        var id = items[i].getElementsByTagName("id")[0].textContent;
        var name = items[i].getElementsByTagName("name")[0].textContent;
        var price = items[i].getElementsByTagName("price")[0].textContent;
        var time = items[i].getElementsByTagName("time")[0].textContent;

        // Builds a new HTML list item for the todo-list item
        var newItem = document.createElement("tr");
        newItem.className='block';
        newItem.setAttribute("onclick","selectID(" + id + ")");
        newItem.setAttribute("id","selectID"+id);
        newItem.setAttribute("name","selectID");
        var p = document.createElement("td");
        p.innerHTML = "<img class=\" itembytag \" src=\"/TT/sellingItem/photo/" + id + "\">";
        var name_ = document.createElement("td");
        name_.textContent = name;
        var price_ = document.createElement("td");
        price_.textContent = price;
        var time_ = document.createElement("td");
        time_.textContent = time;

        newItem.appendChild(p);
        newItem.appendChild(name_);
        newItem.appendChild(price_);
        newItem.appendChild(time_);

        // Adds the todo-list item to the HTML list
        list.appendChild(newItem);
    }
}