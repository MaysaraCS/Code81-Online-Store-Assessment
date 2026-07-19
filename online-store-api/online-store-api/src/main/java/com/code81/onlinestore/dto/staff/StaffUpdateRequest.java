package com.code81.onlinestore.dto.staff;

import com.code81.onlinestore.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Admins can change a staff member's role and active status; not their username/email/password here. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;

    @NotNull(message = "Active flag is required")
    private Boolean active;
}
