class SecurityTagLib {
    static namespace = "ifidsec"

    def springSecurityService

    def redirectPage = { attrs ->
        def url = attrs.get('url')
        response.sendRedirect("${request.contextPath}" + url)
    }
}
