package com.maxacm.lr.dto.accounts;
import java.util.Optional;

import com.maxacm.lr.Enum.TypeAccounts.TypeAccount;

public record UpdateAccount(Optional<String> name, Optional<TypeAccount> type) {
}
