<%
    Object u = session.getAttribute("USER");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth");
        return;
    }
    // Có session thì ??a v? /home (controller s? x? lý ti?p)
    response.sendRedirect(request.getContextPath() + "/home");
%>
