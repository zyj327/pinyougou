package com.pyg.core.controller.upload;

import com.pyg.core.entity.Result;
import com.pyg.core.utils.fastDFS.FastDFSClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadFile.do")
    public Result uploadFile(MultipartFile file){
        try {
            // 使用工具类将附件上传
            String conf = "classpath:fastDFS/fdfs_client.conf";
            FastDFSClient fastDFSClient = new FastDFSClient(conf);
            String filename = file.getOriginalFilename();
            String extName = FilenameUtils.getExtension(filename);
            String path = fastDFSClient.uploadFile(file.getBytes(), extName, null);
            path = FILE_SERVER_URL + path;
            return new Result(true,path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败!");
        }
    }
}
