package com.micro.workload.transaction;


import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class TransactionFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String transactionId = UUID.randomUUID().toString();
        TransactionIdHolder.setTransactionId(transactionId);

        try {
            LOGGER.info("Starting transaction with ID: {}", transactionId);
            chain.doFilter(request, response);
        } finally {
            LOGGER.info("Transaction with ID: {} completed.", transactionId);
            TransactionIdHolder.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
