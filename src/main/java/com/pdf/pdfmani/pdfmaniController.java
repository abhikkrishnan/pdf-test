package com.pdf.pdfmani;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.PageSize;

import org.apache.pdfbox.pdmodel.common.PDRectangle;


@RestController
public class pdfmaniController {

    @Autowired
    private pdfmaniService pdfService;

     @GetMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf() throws IOException {
        byte[] pdfContent = pdfService.generatePdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "sample.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/generate-pdf-merged")
    public ResponseEntity<byte[]> generatePdf_merged() throws IOException {
        byte[] pdfContent = pdfService.generatePdf_merged();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "sample.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/generate-lorem-ipsum-pdf")
    public ResponseEntity<byte[]> generateLoremIpsumPdf() throws IOException {
        String title = "Sample PDF with Lorem Ipsum Text";
        
        List<String> paragraphs = Arrays.asList(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus lacinia odio vitae vestibulum vestibulum. Cras venenatis euismod malesuada.",
            "Sed sit amet libero purus. Fusce gravida, arcu et imperdiet ullamcorper, mauris augue bibendum massa, a auctor magna dolor id magna.",
            "Donec faucibus malesuada turpis, id malesuada magna auctor quis. Vestibulum sit amet magna nec arcu fermentum efficitur.",
            "Nulla facilisi. Pellentesque non nulla purus. Praesent vehicula urna at lorem ultricies bibendum.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean eget aliquet mi. Vestibulum maximus dictum eleifend. Sed hendrerit massa eleifend hendrerit egestas. Nullam nec leo suscipit, maximus libero at, rutrum orci. Fusce fermentum volutpat nulla, eu vulputate ipsum laoreet non. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Sed leo ante, accumsan eu tincidunt sed, interdum eget nisi. Integer tortor lacus, maximus a imperdiet vel, commodo vel enim. In hac habitasse platea dictumst. Praesent ut ipsum vitae dolor consectetur molestie. Vivamus placerat odio nec nulla facilisis commodo." + //
            "Vestibulum erat est, lobortis sed lacinia eget, pellentesque ac felis. Cras aliquet eros ac nunc volutpat dapibus. Morbi ultrices euismod dui eu lacinia. Pellentesque volutpat vestibulum bibendum. Curabitur consectetur suscipit sollicitudin. Vivamus eget condimentum dui. Maecenas suscipit nisi id lorem blandit bibendum. Sed imperdiet risus eu metus vulputate ultrices. Sed dictum justo quis mi porta, quis aliquam nisi ultrices. Fusce at magna sagittis felis pellentesque laoreet. Nunc tortor lacus, finibus at consectetur in, accumsan in magna. Mauris non ipsum eu orci fringilla molestie. Interdum et malesuada fames ac ante ipsum primis in faucibus." + //
            "Mauris vitae vehicula leo. Etiam fringilla ante in erat elementum, ac varius turpis ultricies. Pellentesque imperdiet velit vel nisl tristique, non porttitor libero congue. Sed sit amet nibh volutpat, blandit ligula nec, vestibulum quam. Ut vehicula, turpis non cursus interdum, augue metus condimentum metus, eget placerat mauris ex vitae nisl. Sed sit amet tristique nunc. Sed condimentum purus eros, vel lacinia ex eleifend eget. Vivamus at mauris sed dui egestas luctus sed sit amet urna. Fusce in lectus orci. Mauris mattis augue ac hendrerit tristique." + //
            "Donec eget nisi consectetur, euismod erat quis, egestas tellus. Quisque sit amet semper tellus. Vestibulum at odio viverra metus mattis molestie. Suspendisse fringilla sodales lectus quis posuere. Mauris hendrerit malesuada nisi, vitae scelerisque eros. Sed odio nibh, dapibus quis vulputate quis, ultricies quis dolor. Ut varius mi quis eros egestas suscipit. Phasellus vulputate lectus a tincidunt ultrices." + // 
            "Nullam nec magna lorem. Sed magna nunc, consequat ut lobortis ac, tempor ut arcu. Nam feugiat pellentesque dolor. Donec at aliquet nisl, quis egestas risus. Praesent imperdiet dui in neque tristique, congue dapibus dui feugiat. Cras metus magna, lacinia vel lorem at, rutrum maximus mauris. Ut tellus purus, auctor ut efficitur quis, sollicitudin non neque. Aliquam in enim mauris. Donec auctor tristique pellentesque. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Pellentesque pretium luctus tellus id malesuada. Maecenas nec pretium diam, eget fringilla turpis."+
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean eget aliquet mi. Vestibulum maximus dictum eleifend. Sed hendrerit massa eleifend hendrerit egestas. Nullam nec leo suscipit, maximus libero at, rutrum orci. Fusce fermentum volutpat nulla, eu vulputate ipsum laoreet non. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Sed leo ante, accumsan eu tincidunt sed, interdum eget nisi. Integer tortor lacus, maximus a imperdiet vel, commodo vel enim. In hac habitasse platea dictumst. Praesent ut ipsum vitae dolor consectetur molestie. Vivamus placerat odio nec nulla facilisis commodo." + //
                               
                                "Vestibulum erat est, lobortis sed lacinia eget, pellentesque ac felis. Cras aliquet eros ac nunc volutpat dapibus. Morbi ultrices euismod dui eu lacinia. Pellentesque volutpat vestibulum bibendum. Curabitur consectetur suscipit sollicitudin. Vivamus eget condimentum dui. Maecenas suscipit nisi id lorem blandit bibendum. Sed imperdiet risus eu metus vulputate ultrices. Sed dictum justo quis mi porta, quis aliquam nisi ultrices. Fusce at magna sagittis felis pellentesque laoreet. Nunc tortor lacus, finibus at consectetur in, accumsan in magna. Mauris non ipsum eu orci fringilla molestie. Interdum et malesuada fames ac ante ipsum primis in faucibus." + //
                               
                                "Mauris vitae vehicula leo. Etiam fringilla ante in erat elementum, ac varius turpis ultricies. Pellentesque imperdiet velit vel nisl tristique, non porttitor libero congue. Sed sit amet nibh volutpat, blandit ligula nec, vestibulum quam. Ut vehicula, turpis non cursus interdum, augue metus condimentum metus, eget placerat mauris ex vitae nisl. Sed sit amet tristique nunc. Sed condimentum purus eros, vel lacinia ex eleifend eget. Vivamus at mauris sed dui egestas luctus sed sit amet urna. Fusce in lectus orci. Mauris mattis augue ac hendrerit tristique." + //
                               
                                "Donec eget nisi consectetur, euismod erat quis, egestas tellus. Quisque sit amet semper tellus. Vestibulum at odio viverra metus mattis molestie. Suspendisse fringilla sodales lectus quis posuere. Mauris hendrerit malesuada nisi, vitae scelerisque eros. Sed odio nibh, dapibus quis vulputate quis, ultricies quis dolor. Ut varius mi quis eros egestas suscipit. Phasellus vulputate lectus a tincidunt ultrices." + //
                               
                                "Nullam nec magna lorem. Sed magna nunc, consequat ut lobortis ac, tempor ut arcu. Nam feugiat pellentesque dolor. Donec at aliquet nisl, quis egestas risus. Praesent imperdiet dui in neque tristique, congue dapibus dui feugiat. Cras metus magna, lacinia vel lorem at, rutrum maximus mauris. Ut tellus purus, auctor ut efficitur quis, sollicitudin non neque. Aliquam in enim mauris. Donec auctor tristique pellentesque. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Pellentesque pretium luctus tellus id malesuada. Maecenas nec pretium diam, eget fringilla turpis."
            );

        byte[] pdfContent = pdfService.generatePdfWithTitleAndParagraphs(title, paragraphs);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "lorem_ipsum.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/get-pdf-page")
    public ResponseEntity<byte[]> get_pdf_page(@RequestParam Integer pageIndex) throws IOException {
        byte[] pdfContent = pdfService.generatePdfWithSpecificPage(pageIndex-1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "sample.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/create-pdf-merged")
    public ResponseEntity<byte[]> create_Pdf_merged() throws IOException {
        byte[] pdfContent = pdfService.mergePdfs();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "sample.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/resize-page")
    public ResponseEntity<byte[]> resize_page(@RequestParam String pagesize) throws IOException {

        PDRectangle size = mapPageSize(pagesize);

        byte[] pdfContent = pdfService.resizePdf(size);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "sample.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    // Helper method to map the psize string to a Rectangle object
    private PDRectangle mapPageSize(String psize) {
        switch (psize.toUpperCase()) {
            case "A1": return PDRectangle.A1;
            case "A2": return PDRectangle.A2;
            case "A3": return PDRectangle.A3;
            case "A4": return PDRectangle.A4;
            case "A5": return PDRectangle.A5;
            case "A6": return PDRectangle.A6;
            default: return null; // Return null for invalid page size
        }
    }
    
}
