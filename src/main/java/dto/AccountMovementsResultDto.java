package dto;

import java.util.List;

public class AccountMovementsResultDto {
    private List<AccountMovementRecordDto> movements;

    public List<AccountMovementRecordDto> getMovements() {
        return movements;
    }

    public void setMovements(List<AccountMovementRecordDto> movements) {
        this.movements = movements;
    }
}
