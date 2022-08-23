package com.zekitez.wanddeuze;

import android.app.Application;

public class GlobalFunctions extends Application {

	private final String TAG = "GlobalFunctions";

    @Override
    public void onCreate() {
        super.onCreate();
    }

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		LogThis.d(TAG, "onLowMemory");
		System.gc();
	}

	/////////////////////////////////////////////

	public String getDisclaimerTxt() {
		return "<h1>Disclaimer (in English only)</h1>\n" +
				"<p>Last updated: February 12, 2022</p>\n" +
				"<h1>Interpretation and Definitions</h1>\n" +
				"<h2>Interpretation</h2>\n" +
				"<p>The words of which the initial letter is capitalized have meanings defined under the following conditions.\n" +
				"The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.</p>\n" +
				"<h2>Definitions</h2>\n" +
				"<p>For the purposes of this Disclaimer:</p>\n" +
				"<ul>\n" +
				"<li>\n" +
				"<p><strong>Company</strong> (referred to as either &quot;the Company&quot;, &quot;We&quot;, &quot;Us&quot; or &quot;Our&quot; in this Disclaimer) refers to WandDeuze.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Service</strong> refers to the Application.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>You</strong> means the individual accessing the Service, or the company, or other legal entity on behalf of which such individual is accessing or using the Service, as applicable.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Application</strong> means the software program provided by the Company downloaded by You on any electronic device named WandDeuze.</p>\n" +
				"</li>\n" +
				"</ul>\n" +
				"<h1>Disclaimer</h1>\n" +
				"<p>The information contained on the Service is for general information purposes only.</p>\n" +
				"<p>The Company assumes no responsibility for errors or omissions in the contents of the Service.</p>\n" +
				"<p>In no event shall the Company be liable for any special, direct, indirect, consequential, or incidental damages or any damages whatsoever, whether in an action of contract, negligence or other tort, arising out of or in connection with the use of the Service or the contents of the Service. The Company reserves the right to make additions, deletions, or modifications to the contents on the Service at any time without prior notice. This Disclaimer has been created with the help of the <a href=\"https://www.freeprivacypolicy.com/free-disclaimer-generator/\" target=\"_blank\">Disclaimer Generator</a>.</p>\n" +
				"<p>The Company does not warrant that the Service is free of viruses or other harmful components.</p>\n" +
				"<h1>External Links Disclaimer</h1>\n" +
				"<p>The Service may contain links to external websites that are not provided or maintained by or in any way affiliated with the Company.</p>\n" +
				"<p>Please note that the Company does not guarantee the accuracy, relevance, timeliness, or completeness of any information on these external websites.</p>\n" +
				"<h1>Errors and Omissions Disclaimer</h1>\n" +
				"<p>The information given by the Service is for general guidance on matters of interest only. Even if the Company takes every precaution to insure that the content of the Service is both current and accurate, errors can occur. Plus, given the changing nature of laws, rules and regulations, there may be delays, omissions or inaccuracies in the information contained on the Service.</p>\n" +
				"<p>The Company is not responsible for any errors or omissions, or for the results obtained from the use of this information.</p>\n" +
				"<h1>Fair Use Disclaimer</h1>\n" +
				"<p>The Company may use copyrighted material which has not always been specifically authorized by the copyright owner. The Company is making such material available for criticism, comment, news reporting, teaching, scholarship, or research.</p>\n" +
				"<p>The Company believes this constitutes a &quot;fair use&quot; of any such copyrighted material as provided for in section 107 of the United States Copyright law.</p>\n" +
				"<p>If You wish to use copyrighted material from the Service for your own purposes that go beyond fair use, You must obtain permission from the copyright owner.</p>\n" +
				"<h1>Views Expressed Disclaimer</h1>\n" +
				"<p>The Service may contain views and opinions which are those of the authors and do not necessarily reflect the official policy or position of any other author, agency, organization, employer or company, including the Company.</p>\n" +
				"<p>Comments published by users are their sole responsibility and the users will take full responsibility, liability and blame for any libel or litigation that results from something written in or as a direct result of something written in a comment. The Company is not liable for any comment published by users and reserves the right to delete any comment for any reason whatsoever.</p>\n" +
				"<h1>No Responsibility Disclaimer</h1>\n" +
				"<p>The information on the Service is provided with the understanding that the Company is not herein engaged in rendering legal, accounting, tax, or other professional advice and services. As such, it should not be used as a substitute for consultation with professional accounting, tax, legal or other competent advisers.</p>\n" +
				"<p>In no event shall the Company or its suppliers be liable for any special, incidental, indirect, or consequential damages whatsoever arising out of or in connection with your access or use or inability to access or use the Service.</p>\n" +
				"<h1>&quot;Use at Your Own Risk&quot; Disclaimer</h1>\n" +
				"<p>All information in the Service is provided &quot;as is&quot;, with no guarantee of completeness, accuracy, timeliness or of the results obtained from the use of this information, and without warranty of any kind, express or implied, including, but not limited to warranties of performance, merchantability and fitness for a particular purpose.</p>\n" +
				"<p>The Company will not be liable to You or anyone else for any decision made or action taken in reliance on the information given by the Service or for any consequential, special or similar damages, even if advised of the possibility of such damages.</p>\n" +
				"<h1>Contact Us</h1>\n" +
				"<p>If you have any questions about this Disclaimer, You can contact Us:</p>\n" +
				"<ul>\n" +
				"<li>By email: zekitez@gmail.com</li>\n" +
				"</ul>";
	}
	public String getPrivicyPolicyTxt(){
		return "<h1>Privacy Policy (in English only)</h1>\n" +
				"<p>Last updated: February 12, 2022</p>\n" +
				"<p>This Privacy Policy describes Our policies and procedures on the collection, use and disclosure of Your information when You use the Service and tells You about Your privacy rights and how the law protects You.</p>\n" +
				"<p>We do not collect or use Your Personal data or any other data." +
				"<h1>Interpretation and Definitions</h1>\n" +
				"<h2>Interpretation</h2>\n" +
				"<p>The words of which the initial letter is capitalized have meanings defined under the following conditions. " +
				"The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.</p>\n" +
				"<h2>Definitions</h2>\n" +
				"<p>For the purposes of this Privacy Policy:</p>\n" +
				"<ul>\n" +
				"<li>\n" +
				"<p><strong>Account</strong> means a unique account created for You. We do not provide accounts.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Affiliate</strong> means an entity that controls, is controlled by or is under common control with a party, where &quot;control&quot; means ownership of 50% or more of the shares, equity interest or other securities entitled to vote for election of directors or other managing authority.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Application</strong> means the software program provided by the Company downloaded by You on any electronic device, named WandDeuze</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Company</strong> (referred to as either &quot;the Company&quot;, &quot;We&quot;, &quot;Us&quot; or &quot;Our&quot; in this Agreement) refers to WandDeuze.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Country</strong> refers to:  Netherlands</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Device</strong> means any device that can access the Service such as a computer, a cellphone or a digital tablet.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Personal Data</strong> is any information that relates to an identified or identifiable individual.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Service</strong> refers to the Application.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Service Provider</strong> means any natural or legal person who processes the data on behalf of the Company. It refers to third-party companies or individuals employed by the Company to facilitate the Service, to provide the Service on behalf of the Company, to perform services related to the Service or to assist the Company in analyzing how the Service is used.</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>Usage Data</strong> refers to data collected automatically, either generated by the use of the Service or from the Service infrastructure itself (for example, the duration of a page visit).</p>\n" +
				"</li>\n" +
				"<li>\n" +
				"<p><strong>You</strong> means the individual accessing or using the Service, or the company, or other legal entity on behalf of which such individual is accessing or using the Service, as applicable.</p>\n" +
				"</li>\n" +
				"</ul>\n" +
				"<h2>Types of Data</h2>\n" +
				"<h3>Personal Data</h3>\n" +
				"<p>Personal Data is not collected when using the Service.</p>\n" +
				"<ul>\n" +
				"<h3>Usage Data</h3>\n" +
				"<p>Usage Data is not collected when using the Service.</p>\n" +
				"</ul>\n" +
				"<h3>Any Other Data</h3>\n" +
				"<p>Any Other Data is not collected when using the Service.</p>\n" +
				"</ul>\n" +
				"<h1>Links to Other Websites</h1>\n" +
				"<p>Our Service may contain links to other websites that are not operated by Us. If You click on a third party link, You will be directed to that third party's site. We strongly advise You to review the Privacy Policy of every site You visit.</p>\n" +
				"<p>We have no control over and assume no responsibility for the content, privacy policies or practices of any third party sites or services.</p>\n" +
				"<h1>Changes to this Privacy Policy</h1>\n" +
				"<p>We may update Our Privacy Policy from time to time. We will notify You of any changes by posting the new Privacy Policy on this page.</p>\n" +
				"<p>We will let You know via email and/or a prominent notice on Our Service, prior to the change becoming effective and update the &quot;Last updated&quot; date at the top of this Privacy Policy.</p>\n" +
				"<p>You are advised to review this Privacy Policy periodically for any changes. Changes to this Privacy Policy are effective when they are posted on this page.</p>\n" +
				"<h1>Contact Us</h1>\n" +
				"<p>If you have any questions about this Privacy Policy, You can contact us:</p>\n" +
				"<ul>\n" +
				"<li>By email: zekitez@gmail.com</li>\n" +
				"</ul>";
	};
}
