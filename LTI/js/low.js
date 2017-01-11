clients = [
    {name: 'Allen County', img: 'images/counties/allen.jpg', url:'http://lowtaxinfo.com/allencounty'},
    //Add additional counties between here!!!
     {name: 'Allen County', img: 'images/counties/allen.jpg', url:'http://lowtaxinfo.com/allencounty'},
     {name: 'Allen County', img: 'images/counties/allen.jpg', url:'http://lowtaxinfo.com/allencounty'},
     {name: 'Allen County', img: 'images/counties/allen.jpg', url:'http://lowtaxinfo.com/allencounty'},
    //And here!
    {name: 'More Coming Soon', img: 'images/counties/more.jpg', url:null},
]
    

counties = {
    create: function(client) {
        var div = document.createElement('div');
        if (client.url != null) {
            div.setAttribute('class', 'col-sm-3 col-sm-pull-1 col-sm-offset-1 pointer');
            div.onclick = function() {
                location.replace(client.url);            
            };    
        } else {
        div.setAttribute('class', 'col-sm-3 col-sm-pull-1 col-sm-offset-1');
        }
        
        var img = document.createElement('img');
        img.setAttribute('src', client.img);
        img.setAttribute('class', 'img-circle county-img');

        var h3 = document.createElement('h3');
        h3.innerHTML = client.name;

        div.appendChild(img);        
        div.appendChild(h3);

        return div;
    },
    init: {
        all: function() {
            var client_container = document.getElementById('clients_container');
            for(var i = 0; i < clients.length; i++) {
                client_container.appendChild(counties.create(clients[i]));
            }
            
        }
    }
}

$(function() {
    counties.init.all();
});