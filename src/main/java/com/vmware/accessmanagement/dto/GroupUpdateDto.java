package com.vmware.accessmanagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupUpdateDto {
    String groupDescription;
    //for extending any additional properties of Group which can be updated.
}
