package com.ufcg.si1.resources;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ufcg.si1.model.Produto;
import com.ufcg.si1.model.Registro;
import com.ufcg.si1.repository.RegistroRepository;

@RestController
@RequestMapping("/registro")
@CrossOrigin
public class RegistroResource {

	@Autowired
	private RegistroRepository registroRepository;
		
	@Autowired
	private ApiResource ApiResource;

	@GetMapping(produces="application/json")
	public ResponseEntity<List<Registro>> listarRegistros() {
		List<Registro> registros = registroRepository.findAll();

		if (registros.isEmpty()) {
			return new ResponseEntity<List<Registro>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Registro>>(registros, HttpStatus.OK);
	}

	@GetMapping(value="ultimo",produces="application/json")
	public ResponseEntity<Registro> ultimoRegistro() {
		List<Registro> registros = registroRepository.findAll();
		if(registros.isEmpty()) {
			return new ResponseEntity<Registro>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Registro>(registros.get(registros.size() -1), HttpStatus.OK);
	}

	@PostMapping(produces="application/json")
	public void registrar(@RequestBody Registro registro) {
		List<Produto> produtos = registro.getProdutos();
		Registro registroNovo = new Registro(registro.getData(),registro.getNomeCliente(),registro.getProdutos());
		for (int i = 0; i < produtos.size(); i++) {
			Produto produto = produtos.get(i);
			ApiResource.removerDoLote(produto.getId());
			BigDecimal precoComDesconto = produto.getCategoria().getDesconto().calcularPrecoComDesconto(produto.getPreco());
			registroNovo.setPrecoTotal(precoComDesconto);		
			registroRepository.save(registroNovo);
		}	

	}
}
