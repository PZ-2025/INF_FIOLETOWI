package com.fioletowi.farma.resource;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private Mapper<Resource, ResourceResponse> resourceMapper;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Resource sampleResource;
    private ResourceResponse sampleResourceResp;
    private NewResourceRequest newResourceRequest;
    private UpdateResourceRequest updateResourceRequest;

    @BeforeEach
    void setUp() {
        sampleResource = new Resource();
        sampleResource.setId(1L);
        sampleResource.setName("Resource1");
        sampleResource.setQuantity(BigDecimal.valueOf(10));
        sampleResource.setResourceType(ResourceType.FOR_SELL);

        sampleResourceResp = new ResourceResponse();
        sampleResourceResp.setId(1L);
        sampleResourceResp.setName("Resource1");
        sampleResourceResp.setQuantity(BigDecimal.valueOf(10));
        sampleResourceResp.setResourceType(ResourceType.FOR_SELL);

        newResourceRequest = NewResourceRequest.builder()
                .name("NewResource")
                .quantity(BigDecimal.valueOf(20))
                .resourceType(ResourceType.FOR_SELL)
                .build();

        updateResourceRequest = UpdateResourceRequest.builder()
                .name("UpdatedResource")
                .quantity(BigDecimal.valueOf(30))
                .build();
    }

    @Test
    void findAllResources() {
        var page = new PageImpl<>(List.of(sampleResource));
        given(resourceRepository.findAll(any(Pageable.class))).willReturn(page);
        given(resourceMapper.mapTo(sampleResource, ResourceResponse.class)).willReturn(sampleResourceResp);

        var response = resourceService.findAllResources(PageRequest.of(0, 5));
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().getName()).isEqualTo("Resource1");
    }

    @Test
    void findResourceById_found() {
        given(resourceRepository.findById(1L)).willReturn(Optional.of(sampleResource));
        given(resourceMapper.mapTo(sampleResource, ResourceResponse.class)).willReturn(sampleResourceResp);

        var response = resourceService.findResourceById(1L);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Resource1");
    }

    @Test
    void findResourceById_notFound() {
        given(resourceRepository.findById(5L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> resourceService.findResourceById(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createResource() {
        // Arrange: Create a new resource request and sample resource response
        NewResourceRequest newResourceRequest = NewResourceRequest.builder()
                .name("NewResource") // Resource name
                .quantity(BigDecimal.valueOf(20)) // Quantity
                .resourceType(ResourceType.FOR_SELL) // Set resource type
                .build();

        Resource sampleResource = new Resource();
        sampleResource.setName("NewResource");
        sampleResource.setQuantity(BigDecimal.valueOf(20));
        sampleResource.setResourceType(ResourceType.FOR_SELL);

        ResourceResponse sampleResourceResp = new ResourceResponse();
        sampleResourceResp.setName("NewResource");
        sampleResourceResp.setQuantity(BigDecimal.valueOf(20));

        // Mock repository and mapper behavior
        given(resourceRepository.save(any(Resource.class))).willReturn(sampleResource);
        given(resourceMapper.mapTo(sampleResource, ResourceResponse.class)).willReturn(sampleResourceResp);

        // Act: Create the resource using the service
        ResourceResponse response = resourceService.createResource(newResourceRequest);

        // Assert: Check that the response matches expected values
        assertThat(response.getName()).isEqualTo("NewResource");
        assertThat(response.getQuantity()).isEqualTo(20);
    }

    @Test
    void partialUpdateResource() {
        // Arrange
        sampleResource.setName("Resource1");  // Original name
        sampleResource.setQuantity(BigDecimal.valueOf(10));  // Original quantity

        // Ensure that the repository returns the correct resource before updating
        given(resourceRepository.findById(1L)).willReturn(Optional.of(sampleResource));

        // Set the updated values in the request
        updateResourceRequest = UpdateResourceRequest.builder()
                .name("UpdatedResource")  // Name to be updated
                .quantity(BigDecimal.valueOf(30))  // Updated quantity
                .build();

        // Update the sampleResource object to reflect changes
        sampleResource.setName("UpdatedResource");
        sampleResource.setQuantity(BigDecimal.valueOf(30));

        // Mock saving the updated resource
        given(resourceRepository.save(sampleResource)).willReturn(sampleResource);
        given(resourceMapper.mapTo(sampleResource, ResourceResponse.class)).willReturn(sampleResourceResp);

        // Act
        var response = resourceService.partialUpdateResource(1L, updateResourceRequest);
    }


    @Test
    void deleteResource_notFound() {
        given(resourceRepository.existsById(5L)).willReturn(false);
        assertThatThrownBy(() -> resourceService.deleteResource(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteResource_found() {
        given(resourceRepository.existsById(1L)).willReturn(true);
        willDoNothing().given(resourceRepository).deleteById(1L);

        resourceService.deleteResource(1L);

        then(resourceRepository).should().deleteById(1L);
    }

    @Test
    void findAllResourcesByResourceType() {
        var page = new PageImpl<>(List.of(sampleResource));
        given(resourceRepository.findAllByResourceType(ResourceType.FOR_SELL, PageRequest.of(0, 5)))
                .willReturn(page);
        given(resourceMapper.mapTo(sampleResource, ResourceResponse.class)).willReturn(sampleResourceResp);

        var response = resourceService.findAllResourcesByResourceType(ResourceType.FOR_SELL, PageRequest.of(0, 5));
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().getResourceType()).isEqualTo(ResourceType.FOR_SELL);
    }
}
