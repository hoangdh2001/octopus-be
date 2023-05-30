package com.octopus.dtomodels;

import java.util.Date;

public class ChannelInfo {
    private String _id;
    private String name;
    private String avatar;
    private String lastMessageAt;
    private Date createdAt;
    private Date updatedAt;
    private Boolean hiddenChannel;
    private Boolean activeNotify;
    private UserDTO createdBy;
}
