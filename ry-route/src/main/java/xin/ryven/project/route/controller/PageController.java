package xin.ryven.project.route.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author gray
 */
@Controller
@RequestMapping("page")
@Slf4j
public class PageController {

    @GetMapping("r/{pageName}")
    public void page(@PathVariable String pageName, HttpServletResponse response) throws IOException {
        String content = file(pageName);
        response.setStatus(HttpStatus.OK.value());
        response.addHeader("Content-Type", "text/html; charset=UTF-8");
        response.getOutputStream().write(content.getBytes());
    }

    private String file(String filename) {
        try {
            File file = ResourceUtils.getFile("classpath:static/" + filename + ".html");
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            log.error("文件读取失败", e);
        }
        return "Not found";
    }

}
