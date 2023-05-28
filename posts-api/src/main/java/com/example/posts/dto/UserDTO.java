package com.example.posts.dto;

public record UserDTO(
        Integer id,
        String name,
        String username,
        String email,
        AddressDTO address,
        String phone,
        String website,
        CompanyDTO company
) {
}
