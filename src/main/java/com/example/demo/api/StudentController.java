package com.example.demo.api;

import com.example.demo.Config.RabbitMQConfig;
import com.example.demo.bl.StudentBl;
import com.example.demo.dto.StudentDto;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jrojas
 */
@RestController
@RequestMapping("/v1/api/students")
public class StudentController {

    private static Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    private StudentBl studentBl;

    @Value("${key}")
    String key;

    @Autowired
    public StudentController(StudentBl studentBl) {
        this.studentBl = studentBl;
    }

    @Autowired
    private RabbitTemplate template;

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<PageImpl<StudentDto>> getStudentsPaginate(
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        LOGGER.info("Invocando al servicio REST para obtener el listado de estudiantes con KEY: {}", key);
        PageImpl<StudentDto> studentList = studentBl.getStudentsPaginate(page, size);
        LOGGER.info("Invocacion exitosa para obtener el listado de estudiantes {}", studentList);
        template.convertAndSend(RabbitMQConfig.EXCHNAGE3, RabbitMQConfig.ROUNTING_KEY2+"student", studentList);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
        /**
         * public String producer(@RequestParam("exchangeName") String exchange, @RequestParam("routingKey") String routingKey,
			@RequestParam("messageData") String messageData) {

		amqpTemplate.convertAndSend(exchange, routingKey, messageData)
         */
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Student>> getStudents() {
        LOGGER.info("Invocando al servicio REST para obtener el listado de estudiantes con KEY: {}", key);
        List<Student> studentList = studentBl.getStudents();
        template.convertAndSend(RabbitMQConfig.EXCHNAGE3, RabbitMQConfig.ROUNTING_KEY2+"student", studentList);
        LOGGER.info("Invocacion exitosa para obtener el listado de estudiantes {}", studentList);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Student> getStudentsById(@PathVariable Long id) {
        LOGGER.info("Invocando al servicio REST para obtener el estudiante con id: {}", id);
        Student student = studentBl.getStudentById(id);
        LOGGER.info("Invocacion exitosa para obtener el estudiantes {}", student);
        template.convertAndSend(RabbitMQConfig.EXCHNAGE3, RabbitMQConfig.ROUNTING_KEY2+"student", student);
        template.convertAndSend(RabbitMQConfig.EXCHNAGE2, "", student);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Student> saveStudent(@RequestBody Student student) {
        LOGGER.info("Invocando al servicio REST para registrar un estudiante con la siguiente información: {}", student);
        template.convertAndSend(RabbitMQConfig.EXCHNAGE3, RabbitMQConfig.ROUNTING_KEY3+"student", student);
        template.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUNTING_KEY, student);
        Student result = studentBl.saveStudent(student);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
