(function($) {
    var cometd = $.cometd;
    var serviceName = '/news/new';

    var news_counter = 0;

    $(document).ready(function() {
        function _connectionEstablished() {
            $('#status').html('Connection status: <span style="color:green;">CometD Connection Established</span>');
        }

        function _connectionBroken() {
            $('#status').html('Connection status: <span style="color:red;">CometD Connection Broken</span>');
        }

        function _connectionClosed() {
            $('#status').html('Connection status: <span style="color:red;">CometD Connection Closed</span>');
        }

        // Function that manages the connection status with the Bayeux server
        var _connected = false;
        function _metaConnect(message) {
            if (cometd.isDisconnected()) {
                _connected = false;
                _connectionClosed();
                return;
            }

            var wasConnected = _connected;
            _connected = message.successful === true;
            if (!wasConnected && _connected) {
                _connectionEstablished();
            } else if (wasConnected && !_connected) {
                _connectionBroken();
            }
        }

        // Function invoked when first contacting the server and
        // when the server has lost the state of this client
        function _metaHandshake(handshake) {
            if (handshake.successful === true) {                
                // nothing to do
            }
        }

        // Create a bubble info for the element(s) given by it's(their) class
        function createBubbleinfo(bubbleClass) {
            
            //create bubble popups for each element with class "button"
            $(bubbleClass).CreateBubblePopup();
	
            //set customized mouseover event for each button
            $(bubbleClass).mouseover(function(){
                var html  = $(this).attr('bubble');
                if(  /^\s*$/.test(html)) {
                    html = 'The description is not available';
                }
                //show the bubble popup with new options
                $(this).ShowBubblePopup({
                    innerHtml: html,
                    innerHtmlStyle: {
                        color: '#333333', 
                        'text-align':'center'
                    },										
                    themeName: 	'orange',
                    themePath: 	'resources/images/jquerybubblepopup-theme'								 
                });
            });
        }

        // Disconnect when the page unloads
        $(window).unload(function() {
            cometd.disconnect(true);
        });

        var cometURL = location.protocol + "//" + location.host + config.contextPath + "/cometd";
        cometd.configure({
            url: cometURL,
            logLevel: 'debug'
        });

        cometd.addListener('/meta/handshake', _metaHandshake);
        cometd.addListener('/meta/connect', _metaConnect);
        var counter = 0;
        cometd.handshake();
        cometd.subscribe(serviceName, function(message) {
            
            if(news_counter == 0) {
                $('#news-body').children().first().remove();
                news_counter++;
            }
            
            var timestamp = message.data.timestamp;
            var date = new Date(timestamp);
            var bubbleClass = 'bubbleinfo_' + counter;
            var urlClass = 'go-url_' + (counter++);
            var news = '<tr bubble="' + message.data.description + '" class="' + bubbleClass + '">';
            news +='<td>' + message.data.title + '</td>';
            news += '<td>' + message.data.url + '</td>';
            news += '<td class="date-col">' + date + '</td>';
            news += '<td align="center" class="go"><img url="' + message.data.url + '" src="resources/images/arrow_right.png" alt="visit link" class="go-img ' + urlClass + '" width="20" height="20"/></td></tr>';
            $('#news-body').prepend(news).children().first().hide().show('slow');
            
            // Create bubble info for the new element
            createBubbleinfo('.' + bubbleClass);
            
            $('.' + urlClass).click(function(){
                var url = $(this).attr('url');
                $('#news-frame').attr('src', url);
                $( "#dialog-modal" ).dialog( "open" );
                return false;
            });
            
        });
           
        // Submission of the new news
        $("#news-from").validate({
            submitHandler: function(form) {
                var newsTitle = $('#news-title').val();
                var newsURL = $('#news-url').val();
                var newsDesc = $('#description').val();
                
                // Reset the form
                form.reset();
                
                var timestamp = new Date().getTime();
                cometd.batch(function() {
                    // Publish on a service channel since the message is for the server only
                    cometd.publish(serviceName, {
                        title: newsTitle,
                        url: newsURL,
                        description: newsDesc,
                        timestamp: timestamp
                    });
                });
            }
        });
        
        // Create bubble info for the formular elements
        createBubbleinfo('.bubbleinfo');
        
        $( "#dialog-modal" ).dialog({
            height: 600,
            width: 900,
            modal: true,
            autoOpen: false,
            closeOnEscape: true,
            title: 'URL content'
        });
    });
})(jQuery);
