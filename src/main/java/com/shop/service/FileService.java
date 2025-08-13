package com.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
@Service

@Slf4j
public class FileService {
    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception {

        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        log.info("[방금 저장된 파일명] : {}", savedFileName);

        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);

        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);
        if (deleteFile.exists()) {
            deleteFile.delete();         //파일 경로를 파일 객체로 감싸서 딜리트 메서드 호출하면 삭제된다.
            log.info("[파일 삭제] : {}", filePath);
        }else {
            //유저한테까지는 알림 줄 필요 없고 우리 디버그 할때 편하게 출력 줄건데 sysout 은 사용하면 안된다
            log.info("[존재하지 않는 파일] : {}", filePath);
        }
        //없는 경로 입력하면 exception 생길 건데.
    }

}
