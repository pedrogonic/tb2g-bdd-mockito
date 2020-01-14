package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController ownerController;

    @Mock
    BindingResult bindingResult;

    @Captor
    ArgumentCaptor<String> captor;

    @Test
    void processFindFormWildcardStringWithAnnotation() {
        // given
        Owner owner = new Owner(1l, "Joe", "Buck");
        List<Owner> ownerList = new ArrayList<>();
        given(ownerService.findAllByLastNameLike(captor.capture())).willReturn(ownerList);

        // when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        // then
        assertThat("%Buck%").isEqualToIgnoringCase(captor.getValue());
    }

    @Test
    void processFindFormWildcardString() {
        // given
        Owner owner = new Owner(1l, "Joe", "Buck");
        List<Owner> ownerList = new ArrayList<>();
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        given(ownerService.findAllByLastNameLike(captor.capture())).willReturn(ownerList);

        // when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        // then
        assertThat("%Buck%").isEqualToIgnoringCase(captor.getValue());
    }

    @Test
    void processCreationFormHasErrors() {
        // given
        Owner owner = new Owner(1l, "Bob", "Appleseed");
        given(bindingResult.hasErrors()).willReturn(true);

        // when
        String viewName = ownerController.processCreationForm(owner, bindingResult);

        // then
        assertEquals(OWNERS_CREATE_OR_UPDATE_OWNER_FORM, viewName);
    }

    @Test
    void processCreationFormNoErrors() {
        // given
        Owner owner = new Owner(5l, "Bob", "Appleseed");
        given(bindingResult.hasErrors()).willReturn(false);
        given(ownerService.save(any())).willReturn(owner);

        // when
        String viewName = ownerController.processCreationForm(owner, bindingResult);

        // then
        assertEquals(REDIRECT_OWNERS_5, viewName);
    }
}