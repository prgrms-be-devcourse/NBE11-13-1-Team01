package com.composebean.order.dto;

import com.composebean.order.domain.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "배달 상태 수정 요청")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryStatusUpdateRequest {

    @Schema(description = "수정할 배송 상태", example = "SHIPPING")
    @NotNull
    private DeliveryStatus deliveryStatus;
}
