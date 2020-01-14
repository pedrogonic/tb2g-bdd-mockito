package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

    @Mock(lenient = true)
    OwnerService ownerService;

    @Mock
    Model model;

    @InjectMocks
    OwnerController ownerController;

    @Mock
    BindingResult bindingResult;

    @Captor
    ArgumentCaptor<String> captor;

    @BeforeEach
    void setUp() {
        given(ownerService.findAllByLastNameLike(captor.capture()))
                .willAnswer(invocation -> {
                    List<Owner> ownerList = new ArrayList<>();

                    String name = invocation.getArgument(0);

                    if(name.equals("%Buck%")) {
                        ownerList.add(new Owner(1l, "Joe", "Buck"));
                        return ownerList;
                    } else if (name.equals("%Dontfindme%")) {
                        return ownerList;
                    } else if (name.equals("%Findme%")) {
                        ownerList.add(new Owner(1l, "Joe", "Findme"));
                        ownerList.add(new Owner(2l, "Joe", "Findme"));
                        return ownerList;
                    }

                        throw new RuntimeException("Invalid Argument");
        });
    }

    @Test
    void processFindFormWildcardStringWithAnnotation() {
        // given
        Owner owner = new Owner(1l, "Joe", "Buck");

        // when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        // then
        assertThat("%Buck%").isEqualToIgnoringCase(captor.getValue());
        assertThat("redirect:/owners/1").isEqualToIgnoringCase(viewName);
    }

    @Test
    void processFindFormWildcardStringNotFound() {
        // given
        Owner owner = new Owner(1l, "Joe", "Dontfindme");

        // when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        // then
        assertThat("%Dontfindme%").isEqualToIgnoringCase(captor.getValue());
        assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
    }

    @Test
    void processFindFormWildcardStringFound() {
        // given
        Owner owner = new Owner(1l, "Joe", "Findme");
        InOrder inOrder = Mockito.inOrder(ownerService, model);

        // when
        String viewName = ownerController.processFindForm(owner, bindingResult, model);

        // then
        assertThat("%Findme%").isEqualToIgnoringCase(captor.getValue());
        assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);

        // inOrder asserts
        inOrder.verify(ownerService).findAllByLastNameLike(anyString());
        inOrder.verify(model).addAttribute(anyString(), anyList());
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