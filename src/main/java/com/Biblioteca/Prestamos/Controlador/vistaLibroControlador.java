package com.Biblioteca.Prestamos.Controlador;

import com.Biblioteca.Prestamos.Entidades.Libro;
import com.Biblioteca.Prestamos.Entidades.Usuario;
import com.Biblioteca.Prestamos.Servicios.libroServicio;
import com.Biblioteca.Prestamos.Servicios.usuarioServicio;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class vistaLibroControlador {

    libroServicio servicio;
    usuarioServicio usuServicio;

    public vistaLibroControlador(libroServicio servicio, usuarioServicio usuServicio) {
        this.servicio = servicio;
        this.usuServicio = usuServicio;
    }

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OidcUser principal){
        if(principal != null){
            Usuario user = usuServicio.existeUsuario(principal.getClaims());
            if (user.getNick().equals("jsrb48")){
                System.out.println("Es administrador");
                model.addAttribute("user", user);
                return "index";
            }else {

                model.addAttribute("user", user);
                //System.out.println(principal.getClaims());
                return "Administrador";
            }

        }
        return "index";
    }

    @GetMapping("/Prueba/{nombre}")
    public String prueba(@PathVariable("nombre") String nombre, Model model){
       model.addAttribute("nombre", nombre);
       return "Libros";
    }
    @GetMapping("/Libros")
    public String prueba(Model model){
        List<Libro> lista=this.servicio.listarLibros();
        model.addAttribute("lista", lista);
        return "Libros";
    }

    @GetMapping("/formLibro")
    public String mostrarFormulario(Model model){
        model.addAttribute("libro", new Libro());
        return "registrarLibro";
    }

    @PostMapping("/RegistrarLibro")
    public String agregarLibro(@ModelAttribute("libro") Libro libro, Model model, RedirectAttributes atributes){
        if(servicio.agregarLibro(libro)){
            atributes.addFlashAttribute("mensajeOk","Libro registrado exitosamente.");
        }else {
            atributes.addFlashAttribute("error", "Error, el libro no se registro");
        }
        return "redirect:/Libros";
    }

    @GetMapping("/editarLibro/{isbn}")
    public String pasarLibro(@PathVariable("isbn") String isbn, Model model){
        model.addAttribute("libro",servicio.buscarLibroUno(isbn));
        //return "editarLibro";
        return "editarLibro";
    }

    @GetMapping("/eliminarLibro/{isbn}")
    public String eliminarLibro(@PathVariable("isbn") String isbn, Model model){
        servicio.eliminarLibro(isbn);
        return "redirect:/Libros";
    }

    @PostMapping("/guardarEditado/{isbn}")
    public String actualizarLibro(@PathVariable("isbn") String isbn, @ModelAttribute("libro")Libro libro, Model model){
        Libro lib=servicio.buscarLibroUno(isbn);
        lib.setTitulo(libro.getTitulo());
        lib.setAutor(libro.getAutor());
        lib.setEditorial(libro.getEditorial());
        lib.setNo_page(libro.getNo_page());
        servicio.actualizarLibro(lib);
        return "redirect:/Libros";
    }

}
