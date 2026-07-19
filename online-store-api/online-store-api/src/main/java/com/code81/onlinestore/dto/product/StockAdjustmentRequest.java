package com.code81.onlinestore.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Delta-based stock adjustment: positive to restock, negative to correct a
 * miscount. Order placement/cancellation adjusts stock internally in the
 * order module (phase 5) rather than through this staff-facing endpoint.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequest {

    @NotNull(message = "Quantity delta is required")
    private Integer delta;
}
