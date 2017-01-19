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
        $('.IN_County').prop('style','fill: purple');
        
    }else {
        $('#mobile_only').hide();
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