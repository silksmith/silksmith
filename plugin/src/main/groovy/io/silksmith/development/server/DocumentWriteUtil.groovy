package io.silksmith.development.server

public class DocumentWriteUtil {

	final static def DOCUMENT_WRITE_JS = { location -> """
document.write('<script src="$location"></script>');
""" }
	final static def DOCUMENT_WRITE_CSS = { location -> """
document.write('<link href="$location" rel="stylesheet">');
""" }


	static String write(String location) {
		if(location.endsWith(".js")) {
			return js(location)
		}else if(location.endsWith(".css")) {
			return css(location)
		}
	}
	static String js(String location) {
		return DOCUMENT_WRITE_JS.call(location)
	}
	static String css(String location) {
		return DOCUMENT_WRITE_CSS.call(location)
	}
}
