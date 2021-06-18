package com.valbermedeiros.dscatalog.dto;

public class UserInsertDto extends UserDto{

    private static final long serialVersionUID = 1L;

    private String password;

    public UserInsertDto() {
        super();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}
