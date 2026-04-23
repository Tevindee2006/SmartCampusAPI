package com.smartcampus.api.filter;

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("=== REQUEST: "
                + requestContext.getMethod() + " "
                + requestContext.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        System.out.println("=== RESPONSE STATUS: " + responseContext.getStatus());
    }
}