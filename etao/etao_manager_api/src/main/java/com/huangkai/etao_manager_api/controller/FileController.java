package com.huangkai.etao_manager_api.controller;


import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.FileService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件控制器
 *
 * @author Huangkai on 2022/11/30
 */
@RestController
@RequestMapping("file")
public class FileController {
    @DubboReference
    private FileService fileService;


    /**
     * 上传文件
     * @param file 文件
     * @return 文件路径
     * @throws IOException
     */
    @PostMapping("uploadImage")
    public BaseResult<String> upload(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String url = fileService.uploadImage(bytes,file.getOriginalFilename());
        return BaseResult.ok(url);
    }


    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 操作结果
     */
    @DeleteMapping("delete")
    public BaseResult delete(String filePath){
        fileService.delete(filePath);
        return BaseResult.ok();
    }
}

