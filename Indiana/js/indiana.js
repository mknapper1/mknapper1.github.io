function getSvg(src, parentID) {
    var xhr = new XMLHttpRequest;
    xhr.open("GET", src, false);
    xhr.overrideMimeType('image/svg+xml');
    xhr.send('');
    document.getElementById(parentID).appendChild(xhr.responseXML.documentElement);
}

$(function () {
    getSvg('Indiana.svg', 'SVGContainter');
    
    if((navigator.userAgent.match(/iPhone/i)) || (navigator.userAgent.match(/iPod/i))) {
        document.getElementById('mouseover').disabled = true;
        document.getElementById('click').checked = true;
                alert('here!');

        $('.IN_County').prop('style','fill: green !important');
        document.querySelector('.IN_County').setAttribute('fill','red');
        document.getElementById('IN_Hamilton').setAttribute('fill','yellow');
        document.getElementById('IN_Henry').setAttribute('style', 'fill: blue');
    }else {
        $('#mobile_only').hide();
        $('.IN_County').prop('style','fill: green !important');
        document.querySelector('.IN_County').setAttribute('fill','red');
        document.getElementById('IN_Hamilton').setAttribute('fill','yellow');
        document.getElementById('IN_Henry').setAttribute('style', 'fill: blue');
    }

    $('.IN_County').click(function() {
        if(document.getElementById('click').checked) {
            var id = $(this).prop('id');
            $('.IN_County').prop('style','fill: #E8E8E8');
            $('#County_Name').html('You clicked on ' + id.substring(3).replace('_',' ') + ' county');
            $(this).prop('style','fill: #007f7e');
        }
    });
    $('.IN_County').mouseover(function() {
        if(document.getElementById('mouseover').checked) {
            var id = $(this).prop('id');
            $('.IN_County').prop('style','fill: #E8E8E8');
            $('#County_Name').html('You hovered over ' + id.substring(3).replace('_',' ') + ' county');
            $(this).prop('style','fill: #007f7e');
        }
    });
    
});