clients = [
    {
        name: 'Allen County'
        , img: 'images/counties/allen.jpg'
        , url: 'http://lowtaxinfo.com/allencounty'
    }
    , //Add additional counties between here!!!
    {
        name: 'Sample County'
        , img: 'images/counties/sample/sample1.jpg'
        , url: 'https://www.flickr.com/photos/nostri-imago/3129881412/'
    }
    , {
        name: 'Sample County'
        , img: 'images/counties/sample/sample2.jpg'
        , url: 'https://www.flickr.com/photos/bcgrote/3440470275/'
    }
    
    , //And here!
    {
        name: 'More Coming Soon'
        , img: 'images/counties/more.jpg'
        , url: null
    }

    , ]


counties = {
    create: function (client) {
        var div = document.createElement('div');
        if (client.url != null) {
            div.setAttribute('class', 'col-sm-2 col-sm-offset-1 pointer');
            div.onclick = function () {
                window.location.href = client.url;
            };
        } else {
            div.setAttribute('class', 'col-sm-2 col-sm-offset-1');
        }

        var img = document.createElement('img');
        img.setAttribute('src', client.img);
        img.setAttribute('class', 'img-circle county-img');

        var h4 = document.createElement('h3');
        h4.innerHTML = client.name;

        div.appendChild(img);
        div.appendChild(h4);

        return div;
    }
    , init: {
        all: function () {
            var client_container = document.getElementById('clients_container');
            for (var i = 0; i < clients.length; i++) {
                client_container.appendChild(counties.create(clients[i]));
            }

        }
    }
}

function getSvg(src, parentID) {
    xhr = new XMLHttpRequest;
    xhr.open("GET", src, false);
    xhr.overrideMimeType('image/svg+xml');
    xhr.send('');
    document.getElementById(parentID).appendChild(xhr.responseXML.documentElement);
}

$(function () {
    counties.init.all();
    getSvg('images/Indiana.svg', 'svgcontainer');
});