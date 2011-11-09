<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <link href="${pageContext.request.contextPath}/resources/css/cssLayout.css" rel="stylesheet" type="text/css" />
        <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css" />
        <link href="${pageContext.request.contextPath}/resources/css/jquery.bubblepopup.v2.3.1.css" rel="stylesheet" type="text/css" />
        <link href="${pageContext.request.contextPath}/resources/css/cupertino/jquery-ui-1.8.16.custom.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery-1.6.2.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/json2.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/org/cometd.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery.cometd.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.validate.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.bubblepopup.v2.3.1.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui-cupertino-1.8.16.custom.min.js"></script>

        <script type="text/javascript" src="application.js"></script>
        <%--
        The reason to use a JSP is that it is very easy to obtain server-side configuration
        information (such as the contextPath) and pass it to the JavaScript environment on the client.
        --%>
        <script type="text/javascript">
            var config = {
                contextPath: '${pageContext.request.contextPath}'
            };
            
        </script>
    </head>
    <body>
        <div id="wrapper">
            <div id="wrapper-header">
                <h2 style="color: #FFFFFF">CometD 2 news</h2>
            </div>
            <div id="wrapper-body">
                <div id="news-wrapper" class="ui-widget-content ui-corner-all">
                    <h3 class="ui-widget-header ui-corner-all">News list</h3>
                    <div id="news-feed">
                        <br/>
                        <table border="1" cellpadding="5" cellspacing="0" class="rounded-table" style="border: thin solid silver;width: 99.9%;">
                            <thead class="thead-style">
                                <tr class ="thead-top-tr" style="border-bottom:2px solid silver">
                                    <th>Title</th>
                                    <th>URL</th>
                                    <th class="date-col">Publication date</th>
                                </tr>
                            </thead>
                            <tbody class="tbody-style" id="news-body">
                                <tr><td colspan="3" align="center" style="font-weight: bold">No news available</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div id="pub-news" class="ui-widget-content ui-corner-all">
                    <h3 class="ui-widget-header ui-corner-all">Publish new news</h3>
                    <br/>
                    <form id="news-from" method="POST" action="" class="cmxform">
                        <table>
                            <tbody>
                                <tr>
                                    <td align="right"><label for="news-title">Title<em>*</em>:</label></td>
                                    <td>
                                        <input type="text" value="" name="news-title" class="required bubbleinfo" 
                                               bubble="The title of the news" id="news-title" size="70"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="right"><label for="news-url">URL<em>*</em>:</label></td>
                                    <td>
                                        <input type="text" value="" name="news-url" class="required url bubbleinfo"
                                               bubble="The URL of the news" id="news-url" size="70"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="right" valign="top"><label for="description">Description: </label></td>
                                    <td>  
                                        <textarea id="description" name="description" class="bubbleinfo" bubble="A short description of the news content" rows="7"></textarea>
                                    </td>
                                </tr>
                                <tr><td>&nbsp;</td><td><input type="submit" value="Publish" class="button submit" name="news" id="publish" /></td></tr>
                            </tbody>
                        </table>
                    </form>

                </div>
                <div id="status">
                    Connection status: Establishing connection...
                </div>
            </div>
        </div>

        <div id="dialog-modal">
            <iframe id="news-frame" src="" frameborder="0" scrolling="auto"  width="99.5%" height="99.5%">
                <p>Your browser does not support iframes.</p>
            </iframe>
        </div>
    </body>
</html>
