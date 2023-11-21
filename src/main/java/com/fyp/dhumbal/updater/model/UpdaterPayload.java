package com.fyp.dhumbal.updater.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdaterPayload {
    private UpdateType type;
    private Object data;
}
