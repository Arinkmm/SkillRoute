package com.skillroute.event;

import com.skillroute.model.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRegisteredEvent {
    private Account account;
}
