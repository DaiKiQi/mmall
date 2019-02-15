package com.mmall.service.impI;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.apache.commons.net.ftp.FTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service("fileService")
public class FileServiceImpl implements IFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf('.') + 1);
        String randFileName = new StringBuilder().append(UUID.randomUUID()).append('.').append(fileExtensionName).toString();
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, randFileName);
        try {
            file.transferTo(targetFile);
            //todo upload to ftp server
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //todo delete tomcat file
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }
}
