package dev.masa.masuitechat.core.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import dev.masa.masuitechat.bungee.MaSuiteChat;
import dev.masa.masuitechat.core.models.Bio;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.UUID;

public class BioService {

    @Getter
    private HashMap<UUID, Bio> bios = new HashMap<>();
    private Dao<Bio, Integer> bioDao;

    private MaSuiteChat plugin;

    @SneakyThrows
    public BioService(MaSuiteChat plugin) {
        this.plugin = plugin;
        this.bioDao = DaoManager.createDao(plugin.getApi().getDatabaseService().getConnection(), Bio.class);
        TableUtils.createTableIfNotExists(plugin.getApi().getDatabaseService().getConnection(), Bio.class);
    }

    @SneakyThrows
    public Bio updateBio(Bio bio) {
        bioDao.createOrUpdate(bio);
        bios.put(bio.getOwner(), bio);
        return bio;
    }

    @SneakyThrows
    public Bio getBio(UUID uid) {
        if(bios.containsKey(uid)) return bios.get(uid);
        Bio bio = bioDao.queryBuilder().where().in("owner", uid).queryForFirst();
        if(bio == null) bio = new Bio(uid);

        bios.put(bio.getOwner(), bio);
        return bio;
    }

    public void invalidateCache(UUID uid) {
        bios.remove(uid);
    }

}
