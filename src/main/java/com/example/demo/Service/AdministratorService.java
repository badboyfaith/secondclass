package com.example.demo.Service;
import com.example.demo.Mapper.AdministratorMapper;
import com.example.demo.model.Administrator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.slf4j.Logger; //日志
//import org.slf4j.LoggerFactory;

@Service
public class AdministratorService {
//    private static final Logger logger = LoggerFactory.getLogger(AdministratorService.class);
    @Autowired
    private AdministratorMapper administratorMapper;
    @Transactional
    public boolean register(Administrator admin) {
        Administrator existingAdmin = administratorMapper.findByUsername(admin.getUsername());
        if (existingAdmin != null) {
            return false;
        }
        administratorMapper.insert(admin);
        return true;
    }


    public Administrator login(String username, String password) {
        return administratorMapper.login(username, password);
    }


}
