package com.huangkai.etao_file_service.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.upload.FastFile;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.huangkai.etao_common.exception.BusException;
import com.huangkai.etao_common.result.CodeEnum;
import com.huangkai.etao_common.service.FileService;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.common.value.qual.UnknownVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Huangkai on 2023/5/21
 */
@DubboService
public class FileServiceImpl implements FileService {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Value("${fdfs.fileUrl}")
    private String fileUrl;
    @Override
    public String uploadImage(byte[] fileBytes, String fileName) {
        if (fileBytes.length != 0){
            try {
                //将字节数组文件转为输入流
                InputStream inputStream = new ByteArrayInputStream(fileBytes);
                //获取文件的后缀名，文件名会自己生成，所以只需要给后缀名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                //上传文件
                StorePath storePath = fastFileStorageClient.uploadFile(inputStream, inputStream.available(), fileSuffix, null);
                //返回图片路径
                String imageUrl =fileUrl +"/"+storePath.getFullPath();
                return imageUrl;
            }catch (IOException ioException){
                throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
            }

        }else {
            throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
        }
    }

    @Override
    public void delete(String filePath) {

    }
}

