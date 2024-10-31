    package com.Px4.ChatAPI.controllers.jwt;


    import com.Px4.ChatAPI.config.IgnoreRequest;
    import com.Px4.ChatAPI.config.ResponeMessage;
    import com.Px4.ChatAPI.services.AccountService;
    import com.Px4.ChatAPI.models.jwt.BlackListRepository;
    import com.Px4.ChatAPI.models.account.AccountModel;
    import com.Px4.ChatAPI.models.account.AccountRepository;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.Getter;
    import lombok.Setter;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;
    import java.util.Optional;

    @Component
    public  class JwtRequestFilter extends OncePerRequestFilter {
        @Setter
        @Getter
        private  String idfromJWT;
        @Setter
        @Getter
        private  String jwtToken;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private AccountService accountService;

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private BlackListRepository blackListRepository;

        @Autowired
        private JwtUtil jwtUtil;


        @Autowired
        public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
            this.jwtUtil = jwtUtil;
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            try{
                String requestPath = request.getRequestURI();

                // Bỏ qua các endpoint không yêu cầu xác thực
                if ( IgnoreRequest.isIgnore(requestPath))
                {
                    filterChain.doFilter(request, response);
                    return;
                }

                final String authorizationHeader = request.getHeader("Authorization");

                String username = null;
                String jwt = null;

                // Kiểm tra xem Authorization header có chứa Bearer token hay không
                if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) throw new Exception("Please Login");


                jwt = authorizationHeader.replace("Bearer ", "");  // Bỏ chữ "Bearer " để lấy token

                String idUser = jwtUtil.extractID(jwt); // Xác Thực và Trích xuất username từ token


                if(blackListRepository.existsByToken(jwt)) throw new Exception(ResponeMessage.jwtDeleted); // Check black list of token

                // find username by id của jwt token
                Optional<AccountModel> acc = accountService.getAccountById(idUser);
                username = acc.isPresent() ? acc.get().getId() : null;


                // Xác thực người dùng nếu có token và người dùng chưa được xác thực trong SecurityContextHolder
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null ) {

                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails( request));

                    // Set jwt token and id
                    setIdfromJWT(idUser);
                    setJwtToken(jwt);

                    // Thiết lập đối tượng Authentication vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }
                else{
                    throw new Exception("User not found or have been deleted.");
                }

                // Tiếp tục chuỗi xử lý filter
                filterChain.doFilter(request, response);

            }
            catch (Exception e)
            {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(e.getMessage());
            }

        }


    }