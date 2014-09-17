var req;
var typeOfItem = "sellingItem";

function sendSearchItemByName() {
    
    var keyWords = document.getElementById("searchField").value;

    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    } else {
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }

    req.onreadystatechange = searchResponse;

    req.open("GET", "/TT/"+typeOfItem+"/searchByName/"+keyWords, true);
    req.send();    
}

function sendSearchItemByTag(tag) {
    var keyWords = document.getElementById("searchField").value;

    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    } else {
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }

    req.onreadystatechange = searchResponse;

    req.open("GET", "/TT/"+typeOfItem+"/searchByTag/"+tag, true);
    req.send();    
}

function changeToBuyer(){
	typeOfItem = "sellingItem"
}

function changeToSeller(){
	typeOfItem = "wishingItem"
}

function searchResponse() {
    // window.alert(req.readyState + "   " + req.status);
    if (req.readyState != 4 || req.status != 200) {
        return;
    }

    // var name = document.getElementsByTagName
    var list
    if(typeOfItem == "sellingItem"){
    	list = document.getElementById("product-list");
    }else{
    	list = document.getElementById("product-list2");
    }
    while (list.hasChildNodes()) {
        list.removeChild(list.firstChild);
    }

    // Parses the XML response to get a list of DOM nodes representing items
    var xmlData = req.responseXML;
    var items = xmlData.getElementsByTagName("item");


 	// Adds each new todo-list item to the list
 	for (var i = 0; i < items.length; ++i) {
 		// Parses the item id and text from the DOM
 		var id = items[i].getElementsByTagName("id")[0].textContent;
        var name = items[i].getElementsByTagName("name")[0].textContent;
        var price;
		var downPrice;
		var upPrice;
 		if(typeOfItem =="sellingItem"){
 			price = items[i].getElementsByTagName("price")[0].textContent;
        }

        if(typeOfItem =="wishingItem"){
        	downPrice = items[i].getElementsByTagName("downPrice")[0].textContent;
        	upPrice = items[i].getElementsByTagName("upPrice")[0].textContent;
        }
        
 		var div1 = document.createElement('div');
	    var div2 = document.createElement('div');
	    var div3 = document.createElement('div');
	    var div4 = document.createElement('div');
	    var div5 = document.createElement('div');

	    div1.className = "product-holder";
	    div2.className = "product";
	    div3.className = "desc";
	    div4.className = "bottom";
	    div5.className = "product-bottom";

	    var a1 = document.createElement('a');
        var o1 = document.createElement('object');
	    var p1 = document.createElement('p');
	    var p2 = document.createElement('p');
	    var span1 = document.createElement('span');

	    //I am not sure if it should name or "name"
	    a1.setAttribute("title",name);
	    p1.innerHTML = name;
	    if(typeOfItem == "sellingItem"){
            o1.setAttribute("data","/TT/sellingItem/photo/"+id);
            o1.setAttribute("type","image/jpg");
            o1.setAttribute("class","product-display");
            o1.innerHTML = "<img class=\"product-display\" src=\"/static/img/default.jpg\" type=\"image/jpg\" >";

	    	a1.setAttribute("href","/TT/home/viewSellingItem/"+id);
	    	a1.appendChild(o1);
            p2.innerHTML = "$" + price;
	    }else{
            o1.setAttribute("data","/TT/wishingItem/photo/"+id);
            o1.setAttribute("type","image/jpg");
            o1.setAttribute("class","product-display");
            o1.innerHTML = "<img class=\"product-display\" src=\"/static/img/default.jpg\" type=\"image/jpg\" >";

	    	a1.setAttribute("href","/TT/home/viewWishingItem/"+id);
	    	a1.appendChild(o1);
            p2.innerHTML = "$" + downPrice + "--" + upPrice;
	    }
	    p2.setAttribute("class","price");
	    //p2.appendChild(span1);

	    div3.appendChild(p1);
	    div3.appendChild(p2);

	    div2.appendChild(a1);
	    div2.appendChild(div3);
	    div2.appendChild(div4);

	    div1.appendChild(div2);
	    div1.appendChild(div5);

	    list.appendChild(div1);
 	}
}
    

