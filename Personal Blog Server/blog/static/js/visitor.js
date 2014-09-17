var req;

// send a new request to update the items for visitor
function sendRequest (first_name) {
    if(window.XMLHttpRequest){
        req = new XMLHttpRequest();
    }else{
        req = ActiveXobject("Microsoft.XMLHTTP");
    }
    req.onreadystatechange = handleResponse;
    req.open("GET","/visitorPage/first_name",true);
    req.send();
}


function handleResponse () {
    if (req.readyState != 4 || req.status != 200) {
        return;
    }
    // temp = document.getElementById('visitorPage').getElementsByTagName('tr');
    // for (i in temp) {
    //     if (temp[i].hasOwnProperty) {
    //         tr.push(temp[i]);
    //     }
    // }
    var tblBody = document.getElementsByTagName("tbody");
    //prepare the XML response to get a list of DOM node representing items
    var xmlData = req.responseXML;
    var items = xmlData.getElementsByTagName("item");
    if(items.length == 0){
        var warning = document.getElementById("warning");
        warning.append('You can click the user in the left side bar to view their Recent Postings');
        
    }
    else{
        for (var i = 0; i< items.length; ++i){
        // get the item. title
        var title = items[i].getElementsByTagName("title")[0].textContent;
        var imageID = items[i].getElementsByTagName("img")[0].textContent;
        var pub_date = items[i].getElementsByTagName("pub_date")[0].textContent;

        var row = document.createElement("tr");
        var space1 = document.createElement("td");
        var space2 = document.createElement("td");
        var space3 = document.createElement("td");

        space1.innerHTML = title;
        if (imageID != 0) {
            space2.innerHTML = "<img src='/blog/photo/"+imageID+"' width='200px'>"
        };
        space3.innerHTML = pub_date;
        
        row.appendChild(space1);
        row.appendChild(space2);
        row.appendChild(space3);
        }
        tblBody.appendChild(row);
    }
    
}

