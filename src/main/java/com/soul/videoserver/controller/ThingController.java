package com.soul.videoserver.controller;

import com.soul.videoserver.common.APIResponse;
import com.soul.videoserver.common.ResponeCode;
import com.soul.videoserver.entity.Thing;
import com.soul.videoserver.ffmpeg.FFmpegUtils;
import com.soul.videoserver.ffmpeg.TranscodeConfig;
import com.soul.videoserver.permission.Access;
import com.soul.videoserver.permission.AccessLevel;
import com.soul.videoserver.service.ThingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/thing")
public class ThingController {
    private final TranscodeConfig transcodeConfig = new TranscodeConfig("00:00:00.001","15","","");
    private final static Logger logger = LoggerFactory.getLogger(ThingController.class);
    private Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    @Autowired
    ThingService service;

    @Value("${File.uploadPath}")
    private String uploadPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String c, String tag){
        List<Thing> list =  service.getThingList(keyword, sort, c, tag);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id){
        Thing thing =  service.getThingById(id);
        thing.setPv(String.valueOf(Integer.parseInt(thing.getPv()) + 1));
        service.updateThing(thing);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", thing);
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Thing thing) throws IOException {
        //saveThing(thing);
        saveThingM3U8(thing);
        service.createThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteThing(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Thing thing) throws IOException {
        System.out.println(thing);
//        saveThing(thing);
        service.updateThing(thing);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    public void saveThing(Thing thing) throws IOException {
        // ====================存封面========================
        MultipartFile file = thing.getImageFile();
        String newFileName = null;
        if(file !=null && !file.isEmpty()) {
            // 存文件
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }
        // ====================存视频========================
        MultipartFile rawFile = thing.getRawFile();
        String rawFileName = null;
        if(rawFile !=null && !rawFile.isEmpty()) {
            // 存文件
            String oldFileName = rawFile.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            rawFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "raw" + File.separator + rawFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            rawFile.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(rawFileName)) {
            thing.raw = rawFileName;
        }
    }
    public void saveThingM3U8(Thing thing) throws IOException {
        // ====================存封面========================
        MultipartFile file = thing.getImageFile();
        String newFileName = null;
        if(file !=null && !file.isEmpty()) {
            // 存文件
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }
        // ====================存视频========================
        MultipartFile video = thing.getRawFile();
        logger.info("文件信息：title={}, size={}", video.getOriginalFilename(), video.getSize());
        logger.info("转码配置：{}", transcodeConfig);

        // 原始文件名称，也就是视频的标题
        String title = video.getOriginalFilename();

        // io到临时文件
        Path tempFile = tempDir.resolve(title);
        logger.info("io到临时文件：{}", tempFile.toString());

        try {
            video.transferTo(tempFile);
            // 删除后缀
            title = title.substring(0, title.lastIndexOf("."));

            // 按照日期生成子目录
            String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());

            // 尝试创建视频目录
            Path targetFolder = Files.createDirectories(Paths.get(uploadPath,"raw", today, title));
            logger.info("创建文件夹目录：{}", targetFolder);
            Files.createDirectories(targetFolder);

            // 执行转码操作
            logger.info("开始转码");
            String avatarName = UUID.randomUUID().toString()+".jpg";
            String avatarabPath = uploadPath + File.separator + "image" + File.separator +avatarName;
            logger.info("创建首帧：{}", avatarabPath);
            try {
                FFmpegUtils.transcodeToM3u8(tempFile.toString(), targetFolder.toString(), avatarabPath,transcodeConfig);
            } catch (Exception e) {
                logger.error("转码异常：{}", e.getMessage());
            }
            String filepath = today+File.separator+title;
            String m3u8FilePath = filepath+File.separator+"index.m3u8";
            if(!StringUtils.isEmpty(m3u8FilePath)) {
                thing.raw = m3u8FilePath;
            }
            if(StringUtils.isEmpty(thing.cover)) {
                thing.cover = avatarName;
            }

        } finally {
            // 始终删除临时文件
            Files.delete(tempFile);
        }


    }

}
