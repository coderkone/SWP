<%
    Object u = session.getAttribute("user");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/auth");
        return;
    }
    // C� session th� ??a v? /home (controller s? x? l? ti?p)
    response.sendRedirect(request.getContextPath() + "/home");
%>
