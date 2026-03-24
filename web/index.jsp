<%

    Object u = session.getAttribute("user");
    if (u == null) {
response.sendRedirect(request.getContextPath() + "/home");
return;
    }

%>
