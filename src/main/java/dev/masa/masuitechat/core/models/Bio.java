package dev.masa.masuitechat.core.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Table(name = "masuite_bios")
public class Bio {
    @DatabaseField(generatedId = true)
    private int id;

    @NonNull
    @DatabaseField(dataType = DataType.UUID)
    private UUID owner;

    @DatabaseField
    private String type;

    @DatabaseField
    private String year;

    @DatabaseField
    private String school;

    @DatabaseField
    private String pronouns;

    public boolean hasData() {
        return type != null || year != null || school != null || pronouns != null;
    }
}
