package com.pyg.core.service;

import com.pyg.core.pojo.seller.Seller;
import com.pyg.core.service.seller.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义的认证类
 */
public class UserDetailServiceImpl implements UserDetailsService {

    // 注入sellerService
    private SellerService sellerService;
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
    /**
     * 之前：认证+授权
     * 现在：只需要授权
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 认证用户
        Seller seller = sellerService.findOne(username);
        if(seller != null && "1".equals(seller.getStatus())){ // 用户存在并且是审核通过后的用户
            // 需要对该用户授权,authorities：该用户的权限信息
            Set<GrantedAuthority> authorities = new HashSet<>();
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_SELLER");
            authorities.add(grantedAuthority);
            User user = new User(username, seller.getPassword(), authorities);
            return user;
        }
        return null;
    }
}
