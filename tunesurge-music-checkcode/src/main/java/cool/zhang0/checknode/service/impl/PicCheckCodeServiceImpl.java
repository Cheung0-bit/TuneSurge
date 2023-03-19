package cool.zhang0.checknode.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import cool.zhang0.checknode.model.CheckCodeParamsDto;
import cool.zhang0.checknode.model.CheckCodeResultDto;
import cool.zhang0.checknode.service.AbstractCheckCodeService;
import cool.zhang0.checknode.service.CheckCodeService;
import cool.zhang0.utils.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 图片验证码生成器
 * @date 2022/9/29 16:16
 */
@Service("PicCheckCodeService")
public class PicCheckCodeServiceImpl extends AbstractCheckCodeService implements CheckCodeService {


    @Autowired
    private DefaultKaptcha kaptcha;

    @Resource(name="NumberLetterCheckCodeGenerator")
    @Override
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator = checkCodeGenerator;
    }

    @Resource(name="UUIDKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }


    @Resource(name="MemoryCheckCodeStore")
    @Override
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore = checkCodeStore;
    }


    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        GenerateResult generate = generate(checkCodeParamsDto, 4, "checkcode:", 60);
        String key = generate.getKey();
        String code = generate.getCode();
        String pic = createPic(code);
        CheckCodeResultDto checkCodeResultDto = new CheckCodeResultDto();
        checkCodeResultDto.setAliasing(pic);
        checkCodeResultDto.setKey(key);

        //调用阿里云接口向手机发短信

        return checkCodeResultDto;

    }

    private String createPic(String code) {
        // 生成图片验证码
        ByteArrayOutputStream outputStream = null;
        BufferedImage image = kaptcha.createImage(code);

        outputStream = new ByteArrayOutputStream();
        String imgBase64Encoder = null;
        try {
            // 对字节数组Base64编码
            BASE64Encoder base64Encoder = new BASE64Encoder();
            ImageIO.write(image, "png", outputStream);
            imgBase64Encoder = "data:image/png;base64," + EncryptUtil.encodeBase64(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgBase64Encoder;
    }
}
