package com.alea.pokemon.presentation.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    private RequestLoggingFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new RequestLoggingFilter();
        MDC.clear();
    }

    @Test
    void shouldGenerateCorrelationIdWhenHeaderIsMissing() throws ServletException, IOException {
        when(request.getHeader(RequestLoggingFilter.CORRELATION_ID_HEADER)).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/pokemon/heaviest");
        when(response.getStatus()).thenReturn(200);

        filter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(RequestLoggingFilter.CORRELATION_ID_HEADER), captor.capture());
        assertThat(captor.getValue()).isNotBlank();

        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get(RequestLoggingFilter.CORRELATION_ID_MDC_KEY)).isNull();
    }

    @Test
    void shouldPreserveExistingCorrelationIdWhenHeaderIsProvided() throws ServletException, IOException {
        String existingId = "client-trace-abc-123";
        when(request.getHeader(RequestLoggingFilter.CORRELATION_ID_HEADER)).thenReturn(existingId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/pokemon/highest");
        when(response.getStatus()).thenReturn(200);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(RequestLoggingFilter.CORRELATION_ID_HEADER, existingId);
        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get(RequestLoggingFilter.CORRELATION_ID_MDC_KEY)).isNull();
    }

    @Test
    void shouldGenerateNewCorrelationIdWhenHeaderIsBlank() throws ServletException, IOException {
        when(request.getHeader(RequestLoggingFilter.CORRELATION_ID_HEADER)).thenReturn("   ");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/pokemon/experience");
        when(response.getStatus()).thenReturn(200);

        filter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(RequestLoggingFilter.CORRELATION_ID_HEADER), captor.capture());
        assertThat(captor.getValue()).isNotBlank().isNotEqualTo("   ");

        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get(RequestLoggingFilter.CORRELATION_ID_MDC_KEY)).isNull();
    }

    @Test
    void shouldCleanUpMdcEvenWhenFilterChainThrowsException() throws ServletException, IOException {
        when(request.getHeader(RequestLoggingFilter.CORRELATION_ID_HEADER)).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/pokemon/heaviest");
        when(response.getStatus()).thenReturn(500);

        doThrow(new ServletException("Downstream error")).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(ServletException.class)
                .hasMessage("Downstream error");

        assertThat(MDC.get(RequestLoggingFilter.CORRELATION_ID_MDC_KEY)).isNull();
    }
}

