<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<div class="col-sm-3 col-md-2 sidebar">
  <ul class="nav nav-sidebar">
    <c:choose>
        <c:when test="${menuId==1 }">
        <li class="active"><a href="<c:url value='/cmm/dashboard.do'/>">Main <span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/dashboard.do'/>">Main</a></li>    
        </c:otherwise>
    </c:choose>
    
    <c:choose>
        <c:when test="${menuId==2 }">
        <li class="active"><a href="<c:url value='/cmm/basic/selectItemList.do'/>">Basic Sample<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/basic/selectItemList.do'/>">Basic Sample</a></li>    
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${menuId==3 }">
        <li class="active"><a href="<c:url value='/cmm/board/selectItemList.do'/>">Board<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/board/selectItemList.do'/>">Board</a></li>
   		</c:otherwise>
   </c:choose>  
           
    <c:choose>
	    <c:when test="${menuId==4 }">
	    <li class="active"><a href="<c:url value='/cmm/msg/selectMessageList.do'/>">MessageResource <span class="sr-only">(current)</span></a></li>
	    </c:when>
	    <c:otherwise>
	    <li><a href="<c:url value='/cmm/msg/selectMessageList.do'/>">MessageSource</a></li>    
	    </c:otherwise>
    </c:choose>
                
    <c:choose>
        <c:when test="${menuId==5 }">
        <li class="active"><a href="<c:url value='/cmm/valid/insertItemForm.do'/>">Validation <span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/valid/insertItemForm.do'/>">Validation</a></li>    
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${menuId==6 }">
        <li class="active"><a href="<c:url value='/cmm/mybatis/selectMyBatisList.do'/>">MyBatis <span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/mybatis/selectMyBatisList.do'/>">MyBatis</a></li>   
        </c:otherwise>
    </c:choose>
    
    <c:choose>
        <c:when test="${menuId==7 }">
        <li class="active"><a href="<c:url value='/cmm/excel/selectExcelList.do'/>">Excel <span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/excel/selectExcelList.do'/>">Excel</a></li>   
        </c:otherwise>
    </c:choose>
    
    <c:choose>
        <c:when test="${menuId==8 }">
        <li class="active"><a href="<c:url value='/cmm/file/selectFileList.do'/>">File Up/Down<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/file/selectFileList.do'/>">File Up/Down</a></li>   
        </c:otherwise>
    </c:choose>
    
    <c:choose>
        <c:when test="${menuId==9 }">
        <li class="active"><a href="<c:url value='/cmm/file/selectFileListMin.do'/>">File Up/Down (Min)<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/file/selectFileListMin.do'/>">File Up/Down (Min)</a></li>   
        </c:otherwise>
    </c:choose>
    
    <c:choose>
        <c:when test="${menuId==10 }">
        <li class="active"><a href="<c:url value='/cmm/filehandling/list.do'/>">File Handling<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/filehandling/list.do'/>">File Handling</a></li>    
        </c:otherwise>
    </c:choose>
    
    <c:choose>
        <c:when test="${menuId==11 }">
        <li class="active"><a href="<c:url value='/cmm/websecurity/websecuritySample.do'/>">Web Security<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/websecurity/websecuritySample.do'/>">Web Security</a></li>    
        </c:otherwise>
    </c:choose>
    
    <c:choose>
        <c:when test="${menuId==12 }">
        <li class="active"><a href="<c:url value='/cmm/session/sessionSample.do'/>">Session<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/session/sessionSample.do'/>">Session</a></li>    
        </c:otherwise>
    </c:choose>
    
    <c:choose>
        <c:when test="${menuId==13 }">
        <li class="active"><a href="<c:url value='/cmm/crypto/sampleSHA256.do'/>">Encryption & Decryption<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/crypto/sampleSHA256.do'/>">Encryption & Decryption</a></li>    
        </c:otherwise>
    </c:choose>  
   
   <c:choose>
        <c:when test="${menuId==14 }">
        <li class="active"><a href="<c:url value='/cmm/prop/selectProperty.do'/>">Property<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/prop/selectProperty.do'/>">Property</a></li>
   		</c:otherwise>
   </c:choose>    
   
   <c:choose>
        <c:when test="${menuId==15 }">
        <li class="active"><a href="<c:url value='/cmm/cmprs/compressForm.do'/>">Compress/Decompress<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/cmprs/compressForm.do'/>">Compress/Decompress</a></li>
   		</c:otherwise>
   </c:choose>    
   
   <c:choose>
        <c:when test="${menuId==16 }">
        <li class="active"><a href="<c:url value='/cmm/trans/selectTransactionList.do'/>">Transaction<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/trans/selectTransactionList.do'/>">Transaction</a></li>
   		</c:otherwise>
   </c:choose>
   
   <c:choose>
        <c:when test="${menuId==17 }">
        <li class="active"><a href="<c:url value='/cmm/exception/exceptionSample.do'/>">Exception<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/exception/exceptionSample.do'/>">Exception</a></li>
   		</c:otherwise>
   </c:choose>
   
   <c:choose>
        <c:when test="${menuId==18 }">
        <li class="active"><a href="">String Util<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="#">String Util</a></li>
   		</c:otherwise>
   </c:choose>
   
   <c:choose>
        <c:when test="${menuId==19 }">
        <li class="active"><a href="<c:url value='/cmm/aop/AopSampleRegisterForm.do'/>">AOP(Annotation)<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/aop/AopSampleRegisterForm.do'/>">AOP(Annotation)</a></li>
   		</c:otherwise>
   </c:choose>
   
   <c:choose>
        <c:when test="${menuId==20 }">
        <li class="active"><a href="<c:url value='/cmm/aopxml/AopSampleRegisterForm.do'/>">AOP(XML)<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/aopxml/AopSampleRegisterForm.do'/>">AOP(XML)</a></li>
   		</c:otherwise>
   </c:choose>  
   
   <c:choose>
        <c:when test="${menuId==21 }">
        <li class="active"><a href="<c:url value='/cmm/cache/CacheSampleForm.do'/>">Cache<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="<c:url value='/cmm/cache/CacheSampleForm.do'/>">Cache</a></li>
   		</c:otherwise>
   </c:choose>  
   
    <c:choose>
        <c:when test="${menuId==22 }">
        <li class="active"><a href="#">Spring Scheduling<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="#">Spring Scheduling</a></li>
   		</c:otherwise>
   </c:choose> 
   
    <c:choose>
        <c:when test="${menuId==23 }">
        <li class="active"><a href="#">Quartz<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="#">Quartz</a></li>
   		</c:otherwise>
   </c:choose> 
   
   <c:choose>
        <c:when test="${menuId==24 }">
        <li class="active"><a href="#">REST<span class="sr-only">(current)</span></a></li>
        </c:when>
        <c:otherwise>
        <li><a href="#">REST</a></li>
   		</c:otherwise>
   </c:choose>
   
  </ul>
  
</div>