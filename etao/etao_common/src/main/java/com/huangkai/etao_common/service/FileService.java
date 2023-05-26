package com.huangkai.etao_common.service;

import java.io.IOException;

/**
 * @author Huangkai on 2023/5/21
 */
public interface FileService {
    //上传文件
    String uploadImage(byte[] fileBytes,String fileName) throws IOException;

    //删除图片
    void delete(String filePath);
}
