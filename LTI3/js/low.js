String.prototype.capitalize = function () {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

clients = [
    {
        name: 'Allen County'
        , img: 'images/counties/allen.jpg'
        , url: 'http://lowtaxinfo.com/allencounty'
    }
    , 
    //    Add additional counties between here!!! vvvvvvvvvvvvvvv
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

    , //     And here!  ^^^^^^^^^^^^^^^
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
        img.setAttribute('class', 'img-circle county-img fade-in');

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

map = {
    init: function () {
        map.getSvg('images/Indiana100.svg', 'svgcontainer');
        $('.IN_County').click(function () {
            var id = $(this).prop('id');
            var counties = document.getElementsByClassName('IN_County');
            for (var i = 0; i < counties.length; i++) {
                var county = counties[i];
                county.setAttribute('style', 'fill: #E8E8E8');
            }
            document.getElementById(id).setAttribute('style', 'fill: #007f7e');
            $('#county').val(id.substring(3).replace('_', ' '));

        });
        $('#county').on('input propertychange', function (e) {
            var valueChanged = false;

            if (e.type == 'propertychange') {
                valueChanged = e.originalEvent.propertyName == 'value';
            } else {
                valueChanged = true;
            }
            if (valueChanged) {
                var id = 'IN_' + $(this).val().toLowerCase().capitalize();
                var counties = document.getElementsByClassName('IN_County');
                for (var i = 0; i < counties.length; i++) {
                    var county = counties[i];
                    county.setAttribute('style', 'fill: #E8E8E8');
                }
                document.getElementById(id).setAttribute('style', 'fill: #007f7e');
            }
        })


    }
    , getSvg: function (src, parentID) {
        xhr = new XMLHttpRequest;
        xhr.open("GET", src, false);
        xhr.overrideMimeType('image/svg+xml');
        xhr.send('');
        document.getElementById(parentID).appendChild(xhr.responseXML.documentElement);
    }
}

$(function () {
    counties.init.all();
    map.init();
});
