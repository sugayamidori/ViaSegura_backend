package com.github.sugayamidori.viaseguraapi.controller.mappers;

import com.github.sugayamidori.viaseguraapi.controller.dto.SalvarUsuarioDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.UsuarioDTO;
import com.github.sugayamidori.viaseguraapi.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioDTO dto);

    Usuario toEntity(SalvarUsuarioDTO dto);

    UsuarioDTO toDTO(Usuario usuario);
}
